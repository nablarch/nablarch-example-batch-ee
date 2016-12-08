package com.nablarch.example.app.batch.ee.log;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * 例外に応じたログを出力する。
 */
@ApplicationScoped
@Named
public class ErrorLogWriter {

    /** 例外毎のライター定義 */
    private static final List<Writer<?>> WRITERS = new ArrayList<>();

    /** デフォルトのライター */
    private static final Writer<RuntimeException> DEFAULT_WRITER = new SystemErrorLogWriter();

    static {
        WRITERS.add(new OperatorNoticeErrorLogWriter());
        WRITERS.add(new ApplicationErrorLogWriter());
    }

    /**
     * エラーログを出力する。
     *
     * @param exception 例外クラス
     */
    @SuppressWarnings("unchecked")
    public void writeLog(final RuntimeException exception) {
        final Writer<RuntimeException> writer =
                (Writer<RuntimeException>) WRITERS.stream()
                                                  .filter(w -> w.target()
                                                                .isInstance(exception))
                                                  .findFirst()
                                                  .orElse(DEFAULT_WRITER);
        writer.write(exception);
    }

    /**
     * ログを出力する。
     */
    public interface Writer<T extends RuntimeException> {

        /**
         * ログを出力する。
         *
         * @param exception 例外
         */
        void write(T exception);

        /**
         * ログ出力対象の例外クラスを返す。
         *
         * @return 例外クラス
         */
        Class<T> target();
    }
}
