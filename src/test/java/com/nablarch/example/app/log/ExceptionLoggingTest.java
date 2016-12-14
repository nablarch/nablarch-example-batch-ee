package com.nablarch.example.app.log;

import org.jberet.runtime.JobExecutionImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * 例外ログ出しわけのテスト。
 * バッチを起動するだけで、正しく出力されているかはアサートしない。
 * ログを確認すること。
 */
@Ignore
public class ExceptionLoggingTest {

    private JobExecutionImpl execute() throws InterruptedException {
        final JobOperator jobOperator = BatchRuntime.getJobOperator();
        final long jobExecutionId = jobOperator.start("etl-zip-code-csv-to-db-chunk", null);
        final JobExecutionImpl jobExecution = (JobExecutionImpl) jobOperator.getJobExecution(jobExecutionId);
        jobExecution.awaitTermination(0, TimeUnit.SECONDS);  //no timeout
        return jobExecution;
    }

    File inputFile = new File("testdata/input", "KEN_ALL.CSV");
    File tmpFile = new File("testdata/input", "TMP.CSV");

    public void saveInputFile() throws IOException {
        Files.move(inputFile.toPath(), tmpFile.toPath());
    }

    public void loadInputFile() throws IOException {
        inputFile.delete();
        Files.move(tmpFile.toPath(), inputFile.toPath());
    }

    /**
     * 正常終了するため、ログを出さない。
     */
    @Test
    public void testSuccess() throws Exception {
        execute();
    }

    /**
     * 業務エラー。
     * 不正なデータのある入力ファイルを使用して、アプリケーションログが出力されること。
     * また、バリデーションエラーがあると処理を中断するため、システムエラーログも出力される。
     */
    @Test
    public void testApplicationException() throws Exception {
        saveInputFile();
        File invalidFile = new File("src/test/resources/com/nablarch/example/app/log", "KEN_ALL_ERROR.CSV");
        Files.copy(invalidFile.toPath(), inputFile.toPath());

        execute();

        loadInputFile();
    }

    /**
     * オペレーター向けのエラー。
     * 入力ファイルがない場合にオペレータ向けのログ（エラーとメッセージ）が出力されること。
     */
    @Test
    public void testOperatorException() throws Exception {
        saveInputFile();

        execute();

        loadInputFile();
    }

    /**
     * DBにテーブルがない場合を想定しているため、実行前にテーブルを削除すること。
     *
     * システムエラー。
     * DBのテーブルが見つからない場合に、システムエラーログが出力されること。
     */
    @Test
    public void testSystemException() throws Exception {
        execute();
    }
}
