package com.nablarch.example.app.batch.ee.reader;

import java.io.Serializable;
import java.sql.SQLException;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import nablarch.common.dao.UniversalDao;
import nablarch.etl.DatabaseItemReader;
import nablarch.etl.config.DbInputStepConfig;
import nablarch.etl.config.EtlConfig;
import nablarch.etl.config.RootConfig;

import com.nablarch.example.app.batch.ee.ProgressPrinter;

@Named
@Dependent
public class CustomDatabaseItemReader extends DatabaseItemReader {

    @Inject
    private JobContext jobContext;

    @Inject
    private StepContext stepContext;
    
    /** ETLの設定 */
    @EtlConfig
    @Inject
    private RootConfig etlConfig;

    @Override
    public void open(final Serializable checkpoint) throws SQLException {
        final String jobId = jobContext.getJobName();
        final String stepId = stepContext.getStepName();

        final DbInputStepConfig config = etlConfig.getStepConfig(jobId, stepId);

        final long inputCount = UniversalDao.countBySqlFile(config.getBean(), config.getSqlId());

        final ProgressPrinter progressPrinter = new ProgressPrinter(inputCount);
        stepContext.setTransientUserData(progressPrinter);
        super.open(checkpoint);
    }
}
