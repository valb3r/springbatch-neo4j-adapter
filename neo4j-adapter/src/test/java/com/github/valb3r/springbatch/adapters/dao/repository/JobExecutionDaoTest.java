package com.github.valb3r.springbatch.adapters.dao.repository;

import com.github.valb3r.springbatch.adapters.neo4j.ogm.entity.Neo4jJobInstance;
import com.github.valb3r.springbatch.adapters.neo4j.ogm.repository.Neo4jJobExecutionRepository;
import com.github.valb3r.springbatch.adapters.testconfig.common.DbDropper;
import com.github.valb3r.springbatch.adapters.testconfig.neo4j.Neo4jTestApplication;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Neo4jTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class JobExecutionDaoTest {

    private static final String JOB_NAME = "JOB_NAME";
    private static final String PARAM = "LONG_PARAM_VALUE";
    private static final long LONG_PARAM_VAL = 1L;

    @Autowired
    private JobExecutionDao execDao;

    @Autowired
    private StepExecutionDao stepExecDao;

    @Autowired
    private Neo4jJobExecutionRepository neo4jExecs;

    @Autowired
    private JobInstanceDao instanceDao;

    @Autowired
    private DbDropper dropper;

    @AfterEach
    void dropDatabase() {
        dropper.dropDatabase();
    }

    /**
     * Currently we can't assign id to neo4j entity, so there is impedance between spring-batch and neo4j.
     * Problems are not observed at this moment.
     */
    @Test
    void saveJobExecution() {
        var newExec = execution();
        execDao.saveJobExecution(newExec);

        var execs = neo4jExecs.findAll();
        assertThat(execs).hasSize(1);
        var exec = execs.iterator().next();
        assertThat(exec.getJobInstance()).extracting(Neo4jJobInstance::getJobName).isEqualTo(JOB_NAME);
        assertThat(exec.getExitStatus()).isEqualTo(ExitStatus.UNKNOWN);
        assertThat(exec.getStatus()).isEqualTo(BatchStatus.STARTING);
        assertThat(exec.getJobParameters()).isEqualToComparingFieldByField(newExec.getJobParameters());
    }

    @Test
    void updateJobExecution() {
        var newExec = execution();
        execDao.saveJobExecution(newExec);

        ExecutionContext newCtx = new ExecutionContext(params());
        newExec.setExitStatus(ExitStatus.COMPLETED);
        newExec.setStatus(BatchStatus.ABANDONED);
        newExec.setExecutionContext(newCtx);

        execDao.updateJobExecution(newExec);

        var execs = neo4jExecs.findAll();
        assertThat(execs).hasSize(1);
        var exec = execs.iterator().next();
        assertThat(exec.getJobInstance()).extracting(Neo4jJobInstance::getJobName).isEqualTo(JOB_NAME);
        assertThat(exec.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(exec.getStatus()).isEqualTo(BatchStatus.ABANDONED);
        assertThat(exec.getExecutionContext()).isEqualTo(params());
        assertThat(exec.getJobParameters()).isEqualToComparingFieldByField(newExec.getJobParameters());

    }

    /**
     * Currently, it is required to have step executions in order to find the job execution here.
     */
    @Test
    void findJobExecutions() {
        JobParameters parameters = new JobParametersBuilder().addLong(PARAM, LONG_PARAM_VAL).toJobParameters();
        val instance = instanceDao.createJobInstance(JOB_NAME, parameters);

        var execToSave = execution(instance);
        execDao.saveJobExecution(execToSave);
        addStepExecutions(execToSave);

        execToSave = execution(instance);
        execDao.saveJobExecution(execToSave);
        addStepExecutions(execToSave);

        assertThat(execDao.findJobExecutions(instance)).hasSize(2);
    }

    @Test
    void getLastJobExecution() {
        JobParameters parameters = new JobParametersBuilder().addLong(PARAM, LONG_PARAM_VAL).toJobParameters();
        val instance = instanceDao.createJobInstance(JOB_NAME, parameters);

        var execToSaveOne = execution(instance);
        execDao.saveJobExecution(execToSaveOne);
        addStepExecutions(execToSaveOne);

        var execToSaveTwo = execution(instance);
        execDao.saveJobExecution(execToSaveTwo);
        addStepExecutions(execToSaveTwo);

        assertThat(execDao.getLastJobExecution(instance)).isEqualTo(execToSaveTwo);
    }

    @Test
    void findRunningJobExecutions() {
        JobParameters parameters = new JobParametersBuilder().addLong(PARAM, LONG_PARAM_VAL).toJobParameters();
        val instance = instanceDao.createJobInstance(JOB_NAME, parameters);

        var execToSaveOne = execution(instance);
        execToSaveOne.setStartTime(new Date());
        execDao.saveJobExecution(execToSaveOne);
        addStepExecutions(execToSaveOne);

        var execToSaveTwo = execution(instance);
        execToSaveTwo.setStartTime(new Date());
        execDao.saveJobExecution(execToSaveTwo);
        addStepExecutions(execToSaveTwo);

        assertThat(execDao.findRunningJobExecutions(JOB_NAME)).containsExactlyInAnyOrder(execToSaveOne, execToSaveTwo);
    }

    @Test
    void getJobExecution() {
        JobParameters parameters = new JobParametersBuilder().addLong(PARAM, LONG_PARAM_VAL).toJobParameters();
        val instance = instanceDao.createJobInstance(JOB_NAME, parameters);

        var execToSave = execution(instance);
        execToSave.setStartTime(new Date());
        execDao.saveJobExecution(execToSave);

        assertThat(execDao.getJobExecution(execToSave.getId())).isEqualTo(execToSave);
    }

    /**
     * This one is not exactly complete as it does not use version field.
     */
    @Test
    void synchronizeStatus() {
        JobParameters parameters = new JobParametersBuilder().addLong(PARAM, LONG_PARAM_VAL).toJobParameters();
        val instance = instanceDao.createJobInstance(JOB_NAME, parameters);

        var execToSave = execution(instance);
        execToSave.setStartTime(new Date());
        execDao.saveJobExecution(execToSave);

        execToSave.setStatus(BatchStatus.ABANDONED);
        execDao.synchronizeStatus(execToSave);

        assertThat(execDao.getJobExecution(execToSave.getId())).extracting(JobExecution::getStatus)
            .isEqualTo(BatchStatus.ABANDONED);
    }

    private JobExecution execution(JobInstance instance) {
        JobParameters parameters = new JobParametersBuilder().addLong(PARAM, LONG_PARAM_VAL).toJobParameters();
        return new JobExecution(instance, parameters);
    }

    private JobExecution execution() {
        JobParameters parameters = new JobParametersBuilder().addLong(PARAM, LONG_PARAM_VAL).toJobParameters();
        return execution(instanceDao.createJobInstance(JOB_NAME, parameters));
    }

    private Map<String, Object> params() {
        val params = new HashMap<String, Object>();
        params.put("test", "value");
        return params;
    }

    // this one should be removed when we are able to read job execution without step execution
    private void addStepExecutions(JobExecution execution) {
        stepExecDao.saveStepExecution(new StepExecution("step", execution));
    }
}
