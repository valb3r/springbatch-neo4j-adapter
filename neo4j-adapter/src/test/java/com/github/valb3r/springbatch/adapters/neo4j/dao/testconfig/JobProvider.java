package com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig;

import com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig.dto.AssertableJob;
import com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig.dto.ExecStats;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobProvider {

    public static final String STEP_ONE = "STEP_ONE";
    public static final String STEP_TWO = "STEP_TWO";
    public static final String ONE_STEP_TASKLET = "ONE_STEP_TASKLET";
    public static final String TWO_STEPS_TASKLET = "TWO_STEPS_TASKLET";
    public static final String DONE = "DONE";
    public static final String JOB_NAME = "BATCH_JOB";

    private final JobRepository jobRepository;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @SneakyThrows
    public AssertableJob oneStepTaskletJobEmptyParams() {
        val stats = ExecStats.builder().build();

        val job = jobBuilderFactory.get(ONE_STEP_TASKLET)
            .start(stepBuilderFactory.get(STEP_ONE).tasklet((a, b) -> {
                log.info("TASKLET ONE");
                stats.getTaskletsDone().incrementAndGet();
                stats.getResult().set(DONE);
                return null;
            }).build())
            .build();

        val exec = jobRepository.createJobExecution(ONE_STEP_TASKLET, new JobParameters());

        return AssertableJob.builder()
            .stats(stats)
            .execution(exec)
            .job(job)
            .build();
    }

    @SneakyThrows
    public AssertableJob twoStepTaskletJobEmptyParams() {
        val stats = ExecStats.builder().build();

        val job = jobBuilderFactory.get(TWO_STEPS_TASKLET)
            .start(stepBuilderFactory.get(STEP_ONE).tasklet((a, b) -> {
                log.info("TASKLET ONE");
                stats.getTaskletsDone().incrementAndGet();
                return null;
            }).build())
            .next(stepBuilderFactory.get(STEP_TWO).tasklet((a, b) -> {
                log.info("TASKLET TWO");
                stats.getTaskletsDone().incrementAndGet();
                stats.getResult().set(DONE);
                return null;
            }).build())
            .build();

        val exec = jobRepository.createJobExecution(TWO_STEPS_TASKLET, new JobParameters());

        return AssertableJob.builder()
            .stats(stats)
            .execution(exec)
            .job(job)
            .build();
    }
}
