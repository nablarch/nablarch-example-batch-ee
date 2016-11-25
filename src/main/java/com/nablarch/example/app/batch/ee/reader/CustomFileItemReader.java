package com.nablarch.example.app.batch.ee.reader;

import java.io.FileInputStream;
import java.io.Serializable;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import nablarch.common.databind.ObjectMapper;
import nablarch.common.databind.ObjectMapperFactory;
import nablarch.etl.FileItemReader;
import nablarch.etl.config.EtlConfig;
import nablarch.etl.config.FileToDbStepConfig;
import nablarch.etl.config.RootConfig;

import com.nablarch.example.app.batch.ee.ProgressPrinter;

@Named
@Dependent
public class CustomFileItemReader extends FileItemReader{

    /** {@link JobContext} */
    @Inject
    private JobContext jobContext;

    /** {@link StepContext} */
    @Inject
    private StepContext stepContext;

    /** ETLの設定 */
    @EtlConfig
    @Inject
    private RootConfig etlConfig;
    
    @Override
    public void open(final Serializable checkpoint) throws Exception {

        final FileToDbStepConfig config = etlConfig.getStepConfig(jobContext.getJobName(), stepContext.getStepName());

        long inputCount = 0;
        try (final ObjectMapper<?> mapper = ObjectMapperFactory.create(config.getBean(),
                new FileInputStream(config.getFile()))) {
            
            while (mapper.read() != null) {
                inputCount++;
            }
        }

        final ProgressPrinter metrics = new ProgressPrinter(inputCount);
        stepContext.setTransientUserData(metrics);
        
        super.open(checkpoint);
    }
}
