package com.github.valb3r.springbatch.adapters.dao.parametrized;

import com.github.valb3r.springbatch.adapters.testconfig.common.Const;
import com.github.valb3r.springbatch.adapters.testconfig.common.DbDropper;
import com.github.valb3r.springbatch.adapters.testconfig.common.ParametrizedJobProvider;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.CHUNK_ONE;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.DONE;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.ITEMS;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.ONE_STEP_TASKLET;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.READER_WRITER;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.READS_ONE;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.STEP_ONE;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.STEP_TWO;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.TWO_STEPS_TASKLET;
import static com.github.valb3r.springbatch.adapters.testconfig.common.ParametrizedJobProvider.EXPECTED_DATE;
import static com.github.valb3r.springbatch.adapters.testconfig.common.ParametrizedJobProvider.ONE;
import static com.github.valb3r.springbatch.adapters.testconfig.common.ParametrizedJobProvider.TODAY;
import static com.github.valb3r.springbatch.adapters.testconfig.common.ParametrizedJobProvider.TWO;
import static org.assertj.core.api.Assertions.assertThat;

abstract class BaseParametrizedDaoBasedExecutionTest {

    @Autowired
    private ParametrizedJobProvider provider;

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
        val job = provider.oneStepTaskletJobWithParams();
        job.exec();

        assertThat(job.getStats().getResult()).hasValue(DONE);
        assertThat(job.getStats().getTaskletsDone()).hasValue(1);
        assertThat(instanceDao.getJobNames()).containsExactly(ONE_STEP_TASKLET);
        assertThat(executionDao.findJobExecutions(job.getInstance())).hasSize(1);
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_ONE)).isNotNull();
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_TWO)).isNull();
        assertThat(job.getExecution().getStatus()).isEqualTo(BatchStatus.COMPLETED);

        assertThat(job.getExecution().getJobParameters().getDate(TODAY)).isEqualTo(EXPECTED_DATE);
        assertThat(job.getExecution().getJobParameters().getDouble(ONE)).isEqualTo(1.0);
        assertThat(job.getExecution().getJobParameters().getLong(TWO)).isEqualTo(2L);
        assertThat(job.getExecution().getJobParameters().getString(Const.DONE_PARAM)).isEqualTo(Const.DONE);
    }

    @Test
    void runTwoStepsTasklet() {
        val job = provider.twoStepTaskletJobWithParams();
        job.exec();

        assertThat(job.getStats().getResult()).hasValue(DONE);
        assertThat(job.getStats().getTaskletsDone()).hasValue(2);
        assertThat(instanceDao.getJobNames()).containsExactly(TWO_STEPS_TASKLET);
        assertThat(executionDao.findJobExecutions(job.getInstance())).hasSize(1);
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_ONE)).isNotNull();
        assertThat(stepExecutionDao.getLastStepExecution(job.getInstance(), STEP_TWO)).isNotNull();
        assertThat(job.getExecution().getStatus()).isEqualTo(BatchStatus.COMPLETED);

        assertThat(job.getExecution().getJobParameters().getDate(TODAY)).isEqualTo(EXPECTED_DATE);
        assertThat(job.getExecution().getJobParameters().getDouble(ONE)).isEqualTo(1.0);
        assertThat(job.getExecution().getJobParameters().getLong(TWO)).isEqualTo(2L);
        assertThat(job.getExecution().getJobParameters().getString(Const.DONE_PARAM)).isEqualTo(Const.DONE);
    }

    @Test
    void runOneStepReaderWriter() {
        val job = provider.oneStepReaderWriterJobWithParams();
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
        assertThat(job.getExecution().getStatus()).isEqualTo(BatchStatus.COMPLETED);

        assertThat(job.getExecution().getJobParameters().getDate(TODAY)).isEqualTo(EXPECTED_DATE);
        assertThat(job.getExecution().getJobParameters().getDouble(ONE)).isEqualTo(1.0);
        assertThat(job.getExecution().getJobParameters().getLong(TWO)).isEqualTo(2L);
        assertThat(job.getExecution().getJobParameters().getString(Const.DONE_PARAM)).isEqualTo(Const.DONE);
        assertThat(job.getExecution().getExecutionContext().get(ITEMS))
            .isEqualTo("[0,1,2,3,4,5,6,7,8,9]");
    }
}
