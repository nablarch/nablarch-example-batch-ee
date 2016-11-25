package com.nablarch.example.app.batch.ee.batchlet;

import java.io.File;

import javax.batch.api.AbstractBatchlet;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import nablarch.etl.config.EtlConfig;
import nablarch.etl.config.FileToDbStepConfig;
import nablarch.etl.config.RootConfig;

import com.nablarch.example.app.batch.ee.OperationNoticeException;

@Named
@Dependent
public class FileCheckBatchlet extends AbstractBatchlet {
    
    /** {@link JobContext} */
    private final JobContext jobContext;

    /** {@link StepContext} */
    private final StepContext stepContext;

    /** ETLの設定 */
    @EtlConfig
    @Inject
    private RootConfig etlConfig;

    @Inject
    public FileCheckBatchlet(final JobContext jobContext, final StepContext stepContext) {
        this.jobContext = jobContext;
        this.stepContext = stepContext;
    }

    @Override
    public String process() throws Exception {
        final FileToDbStepConfig config = etlConfig.getStepConfig(jobContext.getJobName(), stepContext.getStepName());
        final File inputFile = config.getFile();
        if (!inputFile.exists()) {
            // とりあえずファイルが存在しない場合にログに出すメッセージを。
            throw new OperationNoticeException("入力ファイル(" + inputFile.getAbsolutePath() + ")"
                                               + "が存在しません。正しく受信できているか？権限が正しいか？などを確認してください。");
        }
        return "success";
    }
}
