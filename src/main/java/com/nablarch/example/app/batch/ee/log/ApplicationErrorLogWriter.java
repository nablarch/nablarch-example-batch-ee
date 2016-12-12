package com.nablarch.example.app.batch.ee.log;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.Message;

/**
 * 業務例外をログ出力するクラス。
 */
public class ApplicationErrorLogWriter implements ErrorLogWriter.Writer<ApplicationException> {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get("application-error");

    @Override
    public void write(final ApplicationException exception) {
        exception.getMessages()
                 .stream()
                 .map(Message::formatMessage)
                 .forEach(LOGGER::logInfo);
        LOGGER.logDebug("業務エラーが発生しました。", exception);
    }

    @Override
    public Class<ApplicationException> target() {
        return ApplicationException.class;
    }
}
