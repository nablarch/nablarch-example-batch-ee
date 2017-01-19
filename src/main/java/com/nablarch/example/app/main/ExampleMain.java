package com.nablarch.example.app.main;

import nablarch.core.util.StringUtil;
import org.jberet.runtime.JobExecutionImpl;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import java.util.concurrent.TimeUnit;

/**
 * Exampleバッチアプリケーションの実行クラス。
 * <p/>
 * 実行引数として、対象JOBの名称を指定する。
 * <p/>
 * 終了コードについて<br>
 * <ul>
 *     <li>正常終了：0</li>
 *     <li>異常終了：1</li>
 *     <li>警告終了：2</li>
 * </ul>
 * バリデーションエラーなど警告すべき事項が発生している場合に、警告終了させることができる。
 * 警告終了の方法は以下。<br>
 * 1.対象ステップで "WARNING" を返す。<br>
 * 2.JOB定義ファイルで、対象のstep要素内にend要素を記述する。<br>
 *   <end on="WARNING" exit-status="WARNING"/><br>
 *   ※onにはステップで返却した値を、exit-statusには"WARNING"を指定すること。
 * <p/>
 *
 * @author Nabu Rakutaro
 */
public final class ExampleMain {

    /**
     * 隠蔽コンストラクタ
     */
    private ExampleMain() {}

    /**
     * メインメソッド。
     * @param args JOB名称
     */
    @SuppressWarnings("CallToSystemExit")
    public static void main(final String[] args) {
        if (args.length != 1 || StringUtil.isNullOrEmpty(args[0])) {
            usage();
            return;
        }

        final int status = executeJob(args[0]);
        System.exit(status);
    }

    /**
     * ジョブを実行する
     *
     * @param job ジョブ名
     * @return 終了コード
     */
    @SuppressWarnings("MethodWithMultipleReturnPoints")
    private static int executeJob(final String job) {
        final JobOperator jobOperator = BatchRuntime.getJobOperator();
        final long jobExecutionId = jobOperator.start(job, null);
        final JobExecutionImpl jobExecution = (JobExecutionImpl) jobOperator.getJobExecution(jobExecutionId);
        try {
            jobExecution.awaitTermination(0, TimeUnit.SECONDS);  //no timeout
        } catch (InterruptedException ignored) {
            return 1;
        }

        // バッチステータスがCOMPLETED以外の場合は異常終了
        if (jobExecution.getBatchStatus() != BatchStatus.COMPLETED) {
            return 1;
        }

        // 終了コード判定
        final String status = jobExecution.getExitStatus();
        if (status == null) {
            throw new BatchRuntimeException(String.format("The job did not complete: %s%n", job));
        } else if ("WARNING".equals(status)) {
            // STEPは正常終了しているが、バリデーションエラーなど警告すべき事項が発生している場合
            return 2;
        }
        return 0;
    }

    /**
     * 標準エラー出力を行う。
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private static void usage() {
        System.err.printf("JOB name is invalid. Please pass the JOB name.");
    }
}
