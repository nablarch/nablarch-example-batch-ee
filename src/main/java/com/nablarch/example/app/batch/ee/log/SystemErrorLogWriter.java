package com.nablarch.example.app.batch.ee.log;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;

/**
 * システムエラーのログを出力する。
 */
public class SystemErrorLogWriter implements ErrorLogWriter.Writer<RuntimeException> {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get("system-error");

    @Override
    public void write(final RuntimeException exception) {
        LOGGER.logFatal("予期しないエラーが発生しました。", exception);
    }

    @Override
    public Class<RuntimeException> target() {
        return RuntimeException.class;
    }
}
