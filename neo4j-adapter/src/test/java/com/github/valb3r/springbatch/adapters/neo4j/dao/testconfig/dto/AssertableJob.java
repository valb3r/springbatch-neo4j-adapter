package com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;

@Getter
@Builder
@AllArgsConstructor
public class AssertableJob {

    private final ExecStats stats;
    private final Job job;
    private final JobExecution execution;

    public void exec() {
        job.execute(execution);
    }
}
