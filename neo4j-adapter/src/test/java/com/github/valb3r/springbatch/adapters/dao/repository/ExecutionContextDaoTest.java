package com.github.valb3r.springbatch.adapters.dao.repository;

import com.github.valb3r.springbatch.adapters.testconfig.common.DbDropper;
import com.github.valb3r.springbatch.adapters.testconfig.neo4j.Neo4jTestApplication;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Neo4jTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ExecutionContextDaoTest {

    private static final String STEP_NAME = "The step";
    private static final String STEP_NAME_OTHER = "The other step";
    private static final String JOB_NAME = "The job";

    @Autowired
    private ExecutionContextDao ctxDao;

    @Autowired
    private JobInstanceDao instanceDao;

    @Autowired
    private JobExecutionDao jobExecDao;

    @Autowired
    private StepExecutionDao stepExecDao;

    @Autowired
    private DbDropper dropper;

    @AfterEach
    void dropDatabase() {
        dropper.dropDatabase();
    }

    @Test
    void getExecutionContextFromJobExecution() {
        var newExec = execution();
        jobExecDao.updateJobExecution(newExec);

        assertThat(ctxDao.getExecutionContext(newExec)).isEqualToComparingFieldByField(execCtx());
    }

    @Test
    void getExecutionContextFromStepExecution() {
        var newExec = execution();
        jobExecDao.updateJobExecution(newExec);
        var stepExec = new StepExecution(STEP_NAME, newExec);
        stepExec.setExecutionContext(stepExecCtx());
        stepExecDao.saveStepExecution(stepExec);

        assertThat(ctxDao.getExecutionContext(stepExec)).isEqualToComparingFieldByField(stepExecCtx());
    }

    @Test
    void saveExecutionContextForJobExecution() {
        var newExec = execution();
        jobExecDao.updateJobExecution(newExec);

        newExec.setExecutionContext(newExecCtx());
        ctxDao.saveExecutionContext(newExec);

        assertThat(ctxDao.getExecutionContext(newExec)).isEqualToComparingFieldByField(newExecCtx());
    }

    @Test
    void saveExecutionContextForStepExecution() {
        var newExec = execution();
        jobExecDao.updateJobExecution(newExec);
        var stepExec = new StepExecution(STEP_NAME, newExec);
        stepExec.setExecutionContext(stepExecCtx());
        stepExecDao.saveStepExecution(stepExec);

        stepExec.setExecutionContext(newStepExecCtx());
        ctxDao.saveExecutionContext(stepExec);

        assertThat(ctxDao.getExecutionContext(stepExec)).isEqualToComparingFieldByField(newStepExecCtx());
    }

    @Test
    void saveExecutionContexts() {
        var newExec = execution();
        jobExecDao.updateJobExecution(newExec);
        var stepExecOne = new StepExecution(STEP_NAME, newExec);
        stepExecDao.saveStepExecution(stepExecOne);
        var stepExecTwo = new StepExecution(STEP_NAME_OTHER, newExec);
        stepExecDao.saveStepExecution(stepExecTwo);

        stepExecOne.setExecutionContext(stepExecCtx());
        stepExecTwo.setExecutionContext(newStepExecCtx());
        ctxDao.saveExecutionContexts(Arrays.asList(stepExecOne, stepExecTwo));

        assertThat(ctxDao.getExecutionContext(stepExecOne)).isEqualToComparingFieldByField(stepExecCtx());
        assertThat(ctxDao.getExecutionContext(stepExecTwo)).isEqualToComparingFieldByField(newStepExecCtx());
    }

    @Test
    void updateExecutionContextForJobExecution() {
        var newExec = execution();
        newExec.setExecutionContext(execCtx());
        jobExecDao.updateJobExecution(newExec);

        newExec.setExecutionContext(newExecCtx());
        ctxDao.updateExecutionContext(newExec);

        assertThat(ctxDao.getExecutionContext(newExec)).isEqualToComparingFieldByField(newExecCtx());
    }

    @Test
    void updateExecutionContextForStepExecution() {
        var newExec = execution();
        jobExecDao.updateJobExecution(newExec);
        var stepExec = new StepExecution(STEP_NAME, newExec);
        stepExec.setExecutionContext(stepExecCtx());
        stepExecDao.saveStepExecution(stepExec);

        stepExec.setExecutionContext(newStepExecCtx());
        stepExecDao.updateStepExecution(stepExec);

        assertThat(ctxDao.getExecutionContext(stepExec)).isEqualToComparingFieldByField(newStepExecCtx());
    }

    private JobExecution execution() {
        val instance = instanceDao.createJobInstance(JOB_NAME, new JobParameters());
        val execution = new JobExecution(instance, new JobParameters());
        execution.setExecutionContext(execCtx());
        jobExecDao.saveJobExecution(execution);
        return execution;
    }

    private ExecutionContext execCtx() {
        return new ExecutionContext(params());
    }

    private ExecutionContext stepExecCtx() {
        return new ExecutionContext(paramsStep());
    }

    private ExecutionContext newExecCtx() {
        return new ExecutionContext(newParams());
    }

    private ExecutionContext newStepExecCtx() {
        return new ExecutionContext(newParamsStep());
    }

    private Map<String, Object> params() {
        val params = new HashMap<String, Object>();
        params.put("test", "value");
        return params;
    }

    private Map<String, Object> paramsStep() {
        val params = new HashMap<String, Object>();
        params.put("test123", "value123");
        return params;
    }

    private Map<String, Object> newParams() {
        val params = new HashMap<String, Object>();
        params.put("new-test", "new-value");
        return params;
    }

    private Map<String, Object> newParamsStep() {
        val params = new HashMap<String, Object>();
        params.put("new-test123", "new-value123");
        return params;
    }
}
