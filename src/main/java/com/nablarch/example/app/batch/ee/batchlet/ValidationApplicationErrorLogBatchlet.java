package com.nablarch.example.app.batch.ee.batchlet;

import nablarch.common.dao.DeferredEntityList;
import nablarch.common.dao.EntityUtil;
import nablarch.common.dao.UniversalDao;
import nablarch.core.beans.BeanUtil;
import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.transaction.TransactionContext;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.etl.EtlJobAbortedException;
import nablarch.etl.WorkItem;
import nablarch.etl.config.EtlConfig;
import nablarch.etl.config.RootConfig;
import nablarch.etl.config.ValidationStepConfig;
import nablarch.etl.config.ValidationStepConfig.Mode;

import javax.batch.api.AbstractBatchlet;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.text.MessageFormat;
import java.util.Set;

import static nablarch.etl.EtlUtil.verifyRequired;

/**
 * {@link nablarch.etl.ValidationBatchlet}のログを業務エラーログとして出力するクラス。
 */
@Named
@Dependent
public class ValidationApplicationErrorLogBatchlet extends AbstractBatchlet {

    /** 業務エラーのロガー */
    private static final Logger LOGGER_APP = LoggerManager.get("application-error");

    /** 結果のロガー */
    private static final Logger LOGGER_INFO = LoggerManager.get("etl");

    /** {@link JobContext} */
    @Inject
    private JobContext jobContext;

    /** {@link StepContext} */
    @Inject
    private StepContext stepContext;

    /** ETLの設定 */
    @Inject
    @EtlConfig
    private RootConfig rootConfig;

    @Override
    public String process() throws Exception {

        final ValidationStepConfig stepConfig = rootConfig.getStepConfig(
                jobContext.getJobName(), stepContext.getStepName());

        verifyConfig(stepConfig);

        final Class<?> inputTable = stepConfig.getBean();
        final Class<?> errorTable = stepConfig.getErrorEntity();

        truncateErrorTable(errorTable);

        final ValidationResult validationResult = new ValidationResult();
        final Validator validator = ValidatorUtil.getValidator();

        // 一時テーブルのデータを全て取得しValidationを行う。
        final DeferredEntityList<?> workItems = (DeferredEntityList<?>) UniversalDao.defer().findAll(inputTable);

        for (Object item : workItems) {
            validationResult.incrementCount();

            final WorkItem workItem = (WorkItem) item;
            final Set<ConstraintViolation<WorkItem>> constraintViolations = validator.validate(workItem);

            if (constraintViolations.isEmpty()) {
                continue;
            }

            validationResult.addErrorCount(constraintViolations.size());
            onError(workItem, constraintViolations, errorTable);
            if (isOverLimit(stepConfig, validationResult)) {
                throw new EtlJobAbortedException("number of validation errors has exceeded the maximum number of errors."
                        + " bean class=[" + inputTable.getName() + ']');
            }
        }
        workItems.close();

        deleteErrorRecord(inputTable, errorTable);

        LOGGER_INFO.logInfo(MessageFormat.format(
                "validation result. bean class=[{0}], line count=[{1}], error count=[{2}]",
                inputTable.getName(), validationResult.getLineCount(), validationResult.getErrorCount()));

        // トランザクションをコミットし処理を終了する。
        // トランザクションをコミットしない場合、ジョブを異常終了するモードの場合に、
        // エラーテーブルに格納した情報などが破棄されてしまう。
        commit();

        return buildResult(stepConfig.getMode(), inputTable, validationResult);
    }

    /**
     * エラーテーブルの内容をクリーニングする。
     *
     * @param errorTable エラーテーブルの内容
     */
    private static void truncateErrorTable(final Class<?> errorTable) {
        final AppDbConnection connection = DbConnectionContext.getConnection();
        final SqlPStatement statement = connection.prepareStatement(
                "truncate table " + EntityUtil.getTableNameWithSchema(errorTable));
        statement.executeUpdate();
        commit();
    }

    /**
     * 一時テーブルからエラーのレコード情報を削除する。
     *
     * @param inputTable 一時テーブル
     * @param errorTable エラーテーブル
     */
    private static void deleteErrorRecord(final Class<?> inputTable, final Class<?> errorTable) {
        final AppDbConnection connection = DbConnectionContext.getConnection();
        final SqlPStatement statement = connection.prepareStatement(buildDeleteSql(inputTable, errorTable));
        statement.executeUpdate();
    }

    /**
     * エラーレコードをクリーニングするためのSQL文を構築する。
     *
     * @param inputTable 一時テーブル
     * @param errorTable エラーテーブル
     * @return SQL文
     */
    private static String buildDeleteSql(final Class<?> inputTable, final Class<?> errorTable) {
        return "delete from " + EntityUtil.getTableNameWithSchema(inputTable)
                + " where line_number in (select line_number from " + EntityUtil.getTableNameWithSchema(errorTable) + ')';
    }

    /**
     * 設定値の検証を行う。
     *
     * @param stepConfig 設定値
     */
    private void verifyConfig(final ValidationStepConfig stepConfig) {
        final String jobName = jobContext.getJobName();
        final String stepName = stepContext.getStepName();

        verifyRequired(jobName, stepName, "bean", stepConfig.getBean());
        verifyRequired(jobName, stepName, "errorEntity", stepConfig.getErrorEntity());
        verifyRequired(jobName, stepName, "mode", stepConfig.getMode());
    }

    /**
     * 結果を構築する。
     *
     * @param mode モード
     * @param inputTableEntity 入力Entityクラス
     * @param validationResult バリデーション結果
     * @return 終了ステータス
     */
    private static String buildResult(
            final Mode mode, final Class<?> inputTableEntity, final ValidationResult validationResult) {

        if (validationResult.hasError()) {
            if (mode == Mode.CONTINUE) {
                return "VALIDATION_ERROR";
            } else {
                throw new EtlJobAbortedException(
                        "abort the JOB because there was a validation error."
                                + " bean class=[" + inputTableEntity.getName() + "],"
                                + " error count=[" + validationResult.getErrorCount() + ']');
            }
        } else {
            return "SUCCESS";
        }
    }


    /**
     * Validationエラー時の処理を行う。
     *
     * @param item Validationエラーが発生したアイテム
     * @param constraintViolations Validationのエラー内容
     * @param errorTable エラーテーブルのエンティティクラス
     */
    private static void onError(
            final WorkItem item,
            final Set<ConstraintViolation<WorkItem>> constraintViolations,
            final Class<?> errorTable) {

        for (ConstraintViolation<WorkItem> violation : constraintViolations) {
            LOGGER_APP.logDebug(MessageFormat.format(
                            "validation error has occurred. bean class=[{0}], property name=[{1}], error message=[{2}], line number=[{3}]",
                            item.getClass()
                                    .getName(),
                            violation.getPropertyPath()
                                    .toString(),
                            violation.getMessage(),
                            item.getLineNumber()
                    )
            );

        }

        UniversalDao.insert(BeanUtil.createAndCopy(errorTable, item));
    }

    /**
     * エラーの許容数を超えたか否か。
     *
     * @param stepConfig 設定値
     * @param validationResult バリデーション結果情報
     * @return 超えている場合は{@code true}
     */
    private static boolean isOverLimit(final ValidationStepConfig stepConfig, final ValidationResult validationResult) {
        final Integer errorLimitCount = stepConfig.getErrorLimit();
        if (errorLimitCount == null || errorLimitCount.compareTo(0) < 0) {
            return false;
        }
        return validationResult.getErrorCount() > errorLimitCount;
    }

    /**
     * コミットを行う。
     */
    private static void commit() {
        TransactionContext.getTransaction().commit();
    }

    /**
     * validation結果を保持するクラス。
     */
    private static class ValidationResult {

        /** 行数 */
        private long lineCount;

        /** エラー数 */
        private long errorCount;

        /**
         * 行数をインクリメントする。
         */
        void incrementCount() {
            lineCount++;
        }

        /**
         * エラー数をインクリメントする。
         *
         * @param count 加算するエラー数
         */
        void addErrorCount(final int count) {
            errorCount += count;
        }

        /**
         * エラー数を取得する。
         *
         * @return エラー数
         */
        long getErrorCount() {
            return errorCount;
        }

        /**
         * 行数を取得する。
         * @return 行数
         */
        public long getLineCount() {
            return lineCount;
        }

        /**
         * エラーの有無を取得する。
         *
         * @return エラーがある場合はtrue
         */
        boolean hasError() {
            return errorCount != 0;
        }
    }
}

