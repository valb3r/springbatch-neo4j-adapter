package com.github.valb3r.springbatch.adapters.dao.repository;

import com.github.valb3r.springbatch.adapters.neo4j.ogm.entity.Neo4jJobExecution;
import com.github.valb3r.springbatch.adapters.neo4j.ogm.entity.Neo4jStepExecution;
import com.github.valb3r.springbatch.adapters.neo4j.ogm.repository.Neo4jStepExecutionRepository;
import com.github.valb3r.springbatch.adapters.testconfig.common.DbDropper;
import com.github.valb3r.springbatch.adapters.testconfig.neo4j.Neo4jTestApplication;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Neo4jTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class StepExecutionDaoTest {

    private static final String JOB_NAME = "The job";
    private static final String STEP_NAME = "The step";
    private static final String STEP_NAME_OTHER = "The other step";

    @Autowired
    private Neo4jStepExecutionRepository stepExecutionRepository;

    @Autowired
    private StepExecutionDao execDao;

    @Autowired
    private JobInstanceDao instanceDao;

    @Autowired
    private JobExecutionDao jobExecDao;
    
    @Autowired
    private DbDropper dropper;

    @AfterEach
    void dropDatabase() {
        dropper.dropDatabase();
    }

    @Test
    void saveStepExecution() {
        val execution = execution();
        execDao.saveStepExecution(new StepExecution(STEP_NAME, execution));

        val steps = stepExecutionRepository.findAll();
        assertThat(steps).hasSize(1);
        val step = steps.iterator().next();
        assertThat(step).extracting(Neo4jStepExecution::getStepName).isEqualTo(STEP_NAME);
        assertThat(step)
            .extracting(Neo4jStepExecution::getJobExecution)
            .extracting(Neo4jJobExecution::getId)
            .isEqualTo(execution.getId());
    }

    @Test
    void saveStepExecutions() {
        val execution = execution();
        execDao.saveStepExecutions(Arrays.asList(
            new StepExecution(STEP_NAME, execution),
            new StepExecution(STEP_NAME_OTHER, execution)
        ));

        val steps = stepExecutionRepository.findAll();
        assertThat(steps).hasSize(2);
        assertThat(steps).extracting(Neo4jStepExecution::getStepName)
            .containsExactlyInAnyOrder(STEP_NAME, STEP_NAME_OTHER);
        assertThat(steps)
            .extracting(Neo4jStepExecution::getJobExecution)
            .extracting(Neo4jJobExecution::getId)
            .containsOnly(execution.getId());
    }

    @Test
    void updateStepExecution() {
        val execution = execution();
        val stepExec = new StepExecution(STEP_NAME, execution);
        execDao.saveStepExecution(stepExec);

        stepExec.setCommitCount(99);
        stepExec.setStatus(BatchStatus.ABANDONED);
        stepExec.setExitStatus(ExitStatus.FAILED);
        execDao.updateStepExecution(stepExec);

        val steps = stepExecutionRepository.findAll();
        assertThat(steps).hasSize(1);
        val step = steps.iterator().next();
        assertThat(step).extracting(Neo4jStepExecution::getStepName).isEqualTo(STEP_NAME);
        assertThat(step)
            .extracting(Neo4jStepExecution::getJobExecution)
            .extracting(Neo4jJobExecution::getId)
            .isEqualTo(execution.getId());
        assertThat(step).isEqualToIgnoringGivenFields(stepExec, "jobExecution", "executionContext");
    }

    @Test
    void getStepExecution() {
        val execution = execution();
        val stepExec = new StepExecution(STEP_NAME, execution);
        execDao.saveStepExecution(stepExec);

        assertThat(execDao.getStepExecution(execution, stepExec.getId()))
            .isEqualToIgnoringGivenFields(stepExec, "jobExecution", "executionContext");
    }

    @Test
    void getLastStepExecution() {
        val execution = execution();
        val stepExecOne = new StepExecution(STEP_NAME, execution);
        execDao.saveStepExecution(stepExecOne);
        val stepExecTwo = new StepExecution(STEP_NAME, execution);
        execDao.saveStepExecution(stepExecTwo);

        assertThat(execDao.getLastStepExecution(execution.getJobInstance(), STEP_NAME))
            .isEqualToIgnoringGivenFields(stepExecTwo, "jobExecution", "executionContext");
    }

    @Test
    void addStepExecutions() {
        val execution = execution();
        val stepExec = new StepExecution(STEP_NAME, execution);
        execDao.saveStepExecution(stepExec);
        assertThat(execution.getStepExecutions()).isEmpty();

        execDao.addStepExecutions(execution);

        assertThat(execution.getStepExecutions()).hasSize(1);
        val step = execution.getStepExecutions().iterator().next();
        assertThat(step).isEqualToIgnoringGivenFields(stepExec, "startTime", "jobExecution", "executionContext");
    }

    private JobExecution execution() {
        val instance = instanceDao.createJobInstance(JOB_NAME, new JobParameters());
        val execution = new JobExecution(instance, new JobParameters());
        jobExecDao.saveJobExecution(execution);
        return execution;
    }
}
