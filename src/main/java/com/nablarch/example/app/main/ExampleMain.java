package com.nablarch.example.app.main;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.batch.operations.BatchRuntimeException;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;

import org.jberet.runtime.JobExecutionImpl;

import nablarch.core.util.StringUtil;

import com.nablarch.example.app.batch.ee.Warning;

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
 *
 */
public class ExampleMain {

    /**
     * メインメソッド。
     * @param args JOB名称
     */
    public static void main(final String[] args) {

        // JOBを取得する
        if (args.length != 1) {
            usage();
            return;
        }
        final String jobXml = args[0];
        if (StringUtil.isNullOrEmpty(jobXml)) {
            usage();
            return;
        }

        // JOBを実行する
        final JobOperator jobOperator = BatchRuntime.getJobOperator();
        final long jobExecutionId;

        try {
            jobExecutionId = jobOperator.start(jobXml, null);
            final JobExecutionImpl jobExecution = (JobExecutionImpl) jobOperator.getJobExecution(jobExecutionId);
            jobExecution.awaitTermination(0, TimeUnit.SECONDS);  //no timeout

            // 終了コード判定
            final String status = jobExecution.getExitStatus();

            if (Objects.equals(status, Warning.STATUS)) {
                // ジョブのステータスがWARNINGの場合は警告終了の2を戻す
                System.exit(2);
            }

            // バッチステータスがCOMPLETED以外の場合は異常終了
            if (jobExecution.getBatchStatus() != BatchStatus.COMPLETED) {
                System.exit(1);
            }
            
            if (status == null) {
                throw new BatchRuntimeException(String.format("The job did not complete: %s%n", jobXml));
            }

        } catch (InterruptedException e) {
            // 処理が中断された場合
            System.exit(1);
        }
    }

    /**
     * 標準エラー出力を行う。
     */
    private static void usage() {
        System.err.printf("JOB name is invalid. Please pass the JOB name.");
    }
}
