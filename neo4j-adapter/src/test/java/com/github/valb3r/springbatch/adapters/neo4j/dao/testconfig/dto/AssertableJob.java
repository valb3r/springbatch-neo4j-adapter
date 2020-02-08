package com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;

@Getter
@Builder
@AllArgsConstructor
public class AssertableJob {

    @NonNull
    private final ExecStats stats;

    @NonNull
    private final Job job;

    @NonNull
    private final JobExecution execution;

    @NonNull
    private final JobInstance instance;

    public void exec() {
        job.execute(execution);
    }
}
