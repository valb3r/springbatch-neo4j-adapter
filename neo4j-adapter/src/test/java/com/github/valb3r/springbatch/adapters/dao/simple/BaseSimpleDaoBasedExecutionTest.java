package com.github.valb3r.springbatch.adapters.dao.simple;

import com.github.valb3r.springbatch.adapters.testconfig.common.DbDropper;
import com.github.valb3r.springbatch.adapters.testconfig.common.JobProvider;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.CHUNK_ONE;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.CHUNK_TWO;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.DONE;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.ONE_STEP_TASKLET;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.READER_WRITER;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.READS_ONE;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.READS_TWO;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.STEP_ONE;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.STEP_TWO;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.TWO_STEPS_TASKLET;
import static org.assertj.core.api.Assertions.assertThat;

abstract class BaseSimpleDaoBasedExecutionTest {

    @Autowired
    private JobProvider provider;

    @Autowired
    private JobInstanceDao instanceDao;

    @Autowired
    private JobExecutionDao executionDao;

    @Autowired
    private StepExecutionDao stepExecutionDao;

    @Autowired
    private DbDropper dropper;

    @AfterEach
    void dropDatabase() {
        dropper.dropDatabase();
    }

    @Test
    void runOneStepTasklet() {
        val job = provider.oneStepTaskletJobEmptyParams();
        job.exec();

        assertThat(job.getStats().getResult()).hasValue(DONE);
        assertThat(job.getStats().getTaskletsDone()).hasValue(1);
        assertThat(instanceDao.getJobNames()).containsExactly(ONE_STEP_TASKLET);
        assertThat(executionDao.findJobExecutions(job.getInstance())).hasSize(1);
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_ONE)).isNotNull();
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_TWO)).isNull();
    }

    @Test
    void runTwoStepsTasklet() {
        val job = provider.twoStepTaskletJobEmptyParams();
        job.exec();

        assertThat(job.getStats().getResult()).hasValue(DONE);
        assertThat(job.getStats().getTaskletsDone()).hasValue(2);
        assertThat(instanceDao.getJobNames()).containsExactly(TWO_STEPS_TASKLET);
        assertThat(executionDao.findJobExecutions(job.getInstance())).hasSize(1);
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_ONE)).isNotNull();
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_TWO)).isNotNull();
    }

    @Test
    void runOneStepReaderWriter() {
        val job = provider.oneStepReaderWriterJobEmptyParams();
        job.exec();

        assertThat(job.getStats().getResult()).hasValue(DONE);
        assertThat(job.getStats().getTaskletsDone()).hasValue(0);
        assertThat(job.getStats().getReads()).hasValue(READS_ONE);
        assertThat(job.getStats().getProcesses()).hasValue(READS_ONE);
        assertThat(job.getStats().getWrites()).hasValue(READS_ONE / CHUNK_ONE);
        assertThat(instanceDao.getJobNames()).containsExactly(READER_WRITER);
        assertThat(executionDao.findJobExecutions(job.getInstance())).hasSize(1);
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_ONE)).isNotNull();
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_TWO)).isNull();
    }

    @Test
    void runTwoStepsReaderWriter() {
        val job = provider.twoStepReaderWriterJobEmptyParams();
        job.exec();

        assertThat(job.getStats().getResult()).hasValue(DONE);
        assertThat(job.getStats().getTaskletsDone()).hasValue(0);
        assertThat(job.getStats().getReads()).hasValue(READS_ONE + READS_TWO);
        assertThat(job.getStats().getProcesses()).hasValue(READS_ONE + READS_TWO);
        assertThat(job.getStats().getWrites()).hasValue(READS_ONE / CHUNK_ONE + READS_TWO / CHUNK_TWO);
        assertThat(instanceDao.getJobNames()).containsExactly(READER_WRITER);
        assertThat(executionDao.findJobExecutions(job.getInstance())).hasSize(1);
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_ONE)).isNotNull();
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_TWO)).isNotNull();
    }
}
