package com.github.valb3r.springbatch.adapters.testconfig.h2;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class H2BatchConfigurer implements BatchConfigurer {

    private final ExecutionContextDao executionContextDao;
    private final JobExecutionDao executionDao;
    private final JobInstanceDao instanceDao;
    private final StepExecutionDao stepExecutionDao;

    @Override
    public JobRepository getJobRepository() {
        return new SimpleJobRepository(instanceDao, executionDao, stepExecutionDao, executionContextDao);
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        // TODO Check transaction management
        return new ResourcelessTransactionManager();
    }

    @Override
    public JobLauncher getJobLauncher() {
        return new SimpleJobLauncher();
    }

    @Override
    public JobExplorer getJobExplorer() {
        return new SimpleJobExplorer(instanceDao, executionDao, stepExecutionDao, executionContextDao);
    }
}

