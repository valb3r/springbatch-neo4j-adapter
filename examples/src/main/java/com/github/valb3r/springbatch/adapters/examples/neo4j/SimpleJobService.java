package com.github.valb3r.springbatch.adapters.examples.neo4j;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

// BEGIN_SNIPPET:Execute simple batch job
@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleJobService {

    @Getter
    private final AtomicReference<String> result = new AtomicReference<>();

    private final JobRepository jobRepository;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @SneakyThrows
    public void runSimpleJob() {
        val job = jobBuilderFactory.get("FOO")
            .start(stepBuilderFactory.get("ONE").tasklet((a, b) -> {
                log.info("STEP ONE!");
                return null;
            }).build())
            .start(stepBuilderFactory.get("TWO").tasklet((a, b) -> {
                log.info("STEP TWO!");
                result.set("Step TWO DONE");
                return null;
            }).build())
            .build();

        val exec = jobRepository.createJobExecution("Test one", new JobParameters());
        job.execute(exec);
    }
}
// END_SNIPPET
