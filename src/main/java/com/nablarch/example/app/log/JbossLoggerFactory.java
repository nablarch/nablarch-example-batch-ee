package com.nablarch.example.app.log;

import nablarch.core.log.LogSettings;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerFactory;

/**
 * {@link JbossLogger}を作成するクラス。
 */
public class JbossLoggerFactory implements LoggerFactory {
    @Override
    public void initialize(LogSettings logSettings) {
    }

    @Override
    public void terminate() {
    }

    @Override
    public Logger get(String name) {
        return new JbossLogger(org.jboss.logging.Logger.getLogger(name));
    }
}
