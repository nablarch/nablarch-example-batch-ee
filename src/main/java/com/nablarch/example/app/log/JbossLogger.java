package com.nablarch.example.app.log;

import org.jboss.logging.Logger.Level;
import nablarch.core.log.Logger;

/**
 * JBossのロガーを使ってログ出力するクラス。
 *
 * {@link org.jboss.logging.Logger}に処理を委譲する。
 */
public class JbossLogger implements Logger {

    /** JBossのロガー */
    private org.jboss.logging.Logger jbossLogger;

    /**
     * コンストラクタ。
     *
     * @param jbossLogger JBossのロガー
     */
    JbossLogger(org.jboss.logging.Logger jbossLogger) {
        this.jbossLogger = jbossLogger;
    }

    @Override
    public boolean isFatalEnabled() {
        return jbossLogger.isEnabled(Level.FATAL);
    }

    @Override
    public void logFatal(String message, Object... options) {
        jbossLogger.fatalv(message, options);
    }

    @Override
    public void logFatal(String message, Throwable error, Object... options) {
        jbossLogger.fatalv(message, error, options);
    }

    @Override
    public boolean isErrorEnabled() {
        return jbossLogger.isEnabled(Level.ERROR);
    }

    @Override
    public void logError(String message, Object... options) {
        jbossLogger.errorv(message, options);
    }

    @Override
    public void logError(String message, Throwable error, Object... options) {
        jbossLogger.errorv(message, error, options);
    }

    @Override
    public boolean isWarnEnabled() {
        return jbossLogger.isEnabled(Level.WARN);
    }

    @Override
    public void logWarn(String message, Object... options) {
        jbossLogger.warnv(message, options);
    }

    @Override
    public void logWarn(String message, Throwable error, Object... options) {
        jbossLogger.warnv(message, error, options);
    }

    @Override
    public boolean isInfoEnabled() {
        return jbossLogger.isEnabled(Level.INFO);
    }

    @Override
    public void logInfo(String message, Object... options) {
        jbossLogger.infov(message, options);
    }

    @Override
    public void logInfo(String message, Throwable error, Object... options) {
        jbossLogger.infov(message, error, options);
    }

    @Override
    public boolean isDebugEnabled() {
        return jbossLogger.isEnabled(Level.DEBUG);
    }

    @Override
    public void logDebug(String message, Object... options) {
        jbossLogger.debugv(message, options);
    }

    @Override
    public void logDebug(String message, Throwable error, Object... options) {
        jbossLogger.debugv(message, error, options);
    }

    @Override
    public boolean isTraceEnabled() {
        return jbossLogger.isEnabled(Level.TRACE);
    }

    @Override
    public void logTrace(String message, Object... options) {
        jbossLogger.tracev(message, options);
    }

    @Override
    public void logTrace(String message, Throwable error, Object... options) {
        jbossLogger.tracev(message, error, options);
    }
}
