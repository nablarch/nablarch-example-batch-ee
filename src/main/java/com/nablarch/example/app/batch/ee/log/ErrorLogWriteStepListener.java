package com.nablarch.example.app.batch.ee.log;

import org.jboss.logging.MDC;

import javax.batch.api.listener.AbstractStepListener;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.UUID;

/**
 * エラーログを出力するステップリスナー実装。
 */
@Named
@Dependent
public class ErrorLogWriteStepListener extends AbstractStepListener {

    /** ステップの情報 */
    private final StepContext stepContext;

    /** エラ情報をログ出力するwriter */
    private final ErrorLogWriter errorLogWriter = new ErrorLogWriter();

    /**
     * コンストラクタ
     *
     * @param stepContext ステップの情報
     */
    @Inject
    public ErrorLogWriteStepListener(final StepContext stepContext) {
        this.stepContext = stepContext;
    }

    @Override
    public void afterStep() throws Exception {
        final Exception exception = stepContext.getException();
        if (exception != null) {
            try {
                MDC.put("logid", UUID.randomUUID());
                errorLogWriter.writeLog(exception);
            } finally {
                MDC.remove("logid");
            }
        }
    }
}
