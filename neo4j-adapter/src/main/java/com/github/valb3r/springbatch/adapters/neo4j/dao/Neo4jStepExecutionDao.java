package com.github.valb3r.springbatch.adapters.neo4j.dao;

import com.github.valb3r.springbatch.adapters.neo4j.ogm.entity.Neo4jStepExecution;
import com.github.valb3r.springbatch.adapters.neo4j.ogm.repository.Neo4jStepExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Neo4jStepExecutionDao implements StepExecutionDao {

    private final Neo4jStepExecutionRepository stepExecs;

    @Override
    @Transactional
    public void saveStepExecution(StepExecution stepExecution) {
        if (null != stepExecution.getId() && stepExecs.existsById(stepExecution.getId())) {
            throw new IllegalStateException("Step execution exists: " + stepExecution.getId());
        }

        stepExecution.incrementVersion();
        val exec = stepExecs.save(Neo4jStepExecution.MAP.map(stepExecution));
        stepExecution.setId(exec.getId());
        stepExecution.setVersion(exec.getVersion());
    }

    @Override
    @Transactional
    public void saveStepExecutions(Collection<StepExecution> stepExecutions) {
        stepExecs.saveAll(
                stepExecutions.stream().map(Neo4jStepExecution.MAP::map).collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public void updateStepExecution(StepExecution stepExecution) {
        val exec = stepExecs.findById(stepExecution.getId())
                .orElseThrow(() -> new IllegalStateException("Step execution does not exist: " + stepExecution.getId()));

        if (!exec.getVersion().equals(stepExecution.getVersion())) {
            throw new OptimisticLockingFailureException("Attempt to update job execution id="
                    + stepExecution.getId() + " with wrong version (" + stepExecution.getVersion()
                    + "), where current version is " + exec.getVersion());
        }

        stepExecution.incrementVersion();
        val updated = stepExecs.save(Neo4jStepExecution.MAP.map(stepExecution));
        stepExecution.setId(updated.getId());
        stepExecution.setVersion(updated.getVersion());
    }

    @Override
    @Transactional
    public StepExecution getStepExecution(JobExecution jobExecution, Long stepExecutionId) {
        return stepExecs.findBy(jobExecution.getId(), stepExecutionId)
                .map(it -> Neo4jStepExecution.MAP.map(it, jobExecution))
                .orElse(null);
    }

    @Override
    @Transactional
    public StepExecution getLastStepExecution(JobInstance jobInstance, String stepName) {
        List<Neo4jStepExecution> executions = stepExecs.findLastStepExecution(jobInstance.getInstanceId(), stepName);

        if (executions.isEmpty()) {
            return null;
        } else {
            return Neo4jStepExecution.MAP.map(executions.get(0));
        }
    }

    @Override
    @Transactional
    public void addStepExecutions(JobExecution jobExecution) {
        stepExecs.findStepExecutions(jobExecution.getId())
                .forEach(it -> {
                            val exec = new StepExecution(
                                    it.getStepName(),
                                    jobExecution,
                                    it.getId());
                            exec.setVersion(it.getVersion());
                        }
                );
    }
}
