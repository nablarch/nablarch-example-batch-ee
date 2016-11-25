package com.nablarch.example.app.batch.ee.listener;

import java.util.List;

import javax.batch.api.chunk.listener.AbstractItemWriteListener;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.nablarch.example.app.batch.ee.ProgressPrinter;

@Named
@Dependent
public class ProgressPrintListener extends AbstractItemWriteListener{

    @Inject
    private StepContext stepContext;

    @Override
    public void afterWrite(final List<Object> items) throws Exception {
        final ProgressPrinter metrics = (ProgressPrinter) stepContext.getTransientUserData();
        metrics.output(stepContext);
    }
}
