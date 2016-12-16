package com.nablarch.example.app.batch.ee.log;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import org.jboss.logging.MDC;

import java.util.Random;

/**
 * システムエラーのログを出力する。
 */
public class SystemErrorLogWriter implements ErrorLogWriter.Writer<Exception> {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get("system-error");

    @Override
    public void write(final Exception exception) {
        MDC.put("logid", new Random().nextInt(100000));
        LOGGER.logFatal("予期しないエラーが発生しました。", exception);
    }

    @Override
    public Class<Exception> target() {
        return Exception.class;
    }
}
