package com.nablarch.example.app.batch.ee.log;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;

import com.nablarch.example.app.batch.ee.OperatorNoticeException;
import org.jboss.logging.MDC;

import java.util.Random;

/**
 * オペレータに通知するためのログを出力する。
 */
public class OperatorNoticeErrorLogWriter implements ErrorLogWriter.Writer<OperatorNoticeException> {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get("operator");

    @Override
    public void write(final OperatorNoticeException exception) {
        MDC.put("logid", new Random().nextInt(100000));
        LOGGER.logError(exception.getMessage(), exception);
    }

    @Override
    public Class<OperatorNoticeException> target() {
        return OperatorNoticeException.class;
    }
}
