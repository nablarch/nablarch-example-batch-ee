package com.nablarch.example.app.batch.ee.log;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;

import com.nablarch.example.app.batch.ee.OperatorNoticeException;

/**
 * オペレータに通知するためのログを出力する。
 */
public class OperatorNoticeErrorLogWriter implements ErrorLogWriter.Writer<OperatorNoticeException> {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get("operator");

    @Override
    public void write(final OperatorNoticeException exception) {
        LOGGER.logError(exception.getMessage(), exception);
    }

    @Override
    public Class<OperatorNoticeException> target() {
        return OperatorNoticeException.class;
    }
}
