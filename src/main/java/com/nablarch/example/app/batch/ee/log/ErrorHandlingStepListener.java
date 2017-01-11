package com.nablarch.example.app.batch.ee.log;

import java.util.UUID;

import javax.batch.api.listener.AbstractStepListener;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.MDC;

import com.nablarch.example.app.batch.ee.Warning;

/**
 * ステップで発生した例外をハンドリングするステップリスナー実装
 */
@Named
@Dependent
public class ErrorHandlingStepListener extends AbstractStepListener {

    /** ステップの情報 */
    private final StepContext stepContext;

    /** ジョブの情報 */
    private final JobContext jobContext;

    /** エラ情報をログ出力するwriter */
    private final ErrorLogWriter errorLogWriter = new ErrorLogWriter();

    /**
     * コンストラクタ
     *
     * @param stepContext ステップの情報
     * @param jobContext ジョブの情報
     */
    @Inject
    public ErrorHandlingStepListener(final StepContext stepContext, final JobContext jobContext) {
        this.stepContext = stepContext;
        this.jobContext = jobContext;
    }

    @Override
    public void afterStep() throws Exception {
        final Exception exception = stepContext.getException();
        if (exception != null) {
            writeLog(exception);
            changeStatus(exception);
        }
    }

    /**
     * バッチステータス及びジョブステータスを変更する。
     * @param exception 例外
     */
    private void changeStatus(final Exception exception) {
        if (!(exception instanceof Warning)) {
            return;
        }
        jobContext.setExitStatus(Warning.STATUS);
        if (stepContext.getExitStatus() != null) {
            stepContext.setExitStatus(Warning.STATUS);
        }
    }

    /**
     * ログを出力する。
     *
     * @param exception 出力対象の例外
     */
    private void writeLog(final Exception exception) {
        try {
            MDC.put("logid", UUID.randomUUID());
            errorLogWriter.writeLog(exception);
        } finally {
            MDC.remove("logid");
        }
    }
}
