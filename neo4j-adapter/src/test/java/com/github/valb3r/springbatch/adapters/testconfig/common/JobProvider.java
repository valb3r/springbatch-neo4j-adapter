package com.github.valb3r.springbatch.adapters.testconfig.common;

import com.github.valb3r.springbatch.adapters.testconfig.common.dto.AssertableJob;
import com.github.valb3r.springbatch.adapters.testconfig.common.dto.ExecStats;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobProvider {

    public static final String STEP_ONE = "STEP_ONE";
    public static final String STEP_TWO = "STEP_TWO";
    public static final String ONE_STEP_TASKLET = "ONE_STEP_TASKLET";
    public static final String TWO_STEPS_TASKLET = "TWO_STEPS_TASKLET";
    public static final String READER_WRITER = "READER_WRITER";
    public static final String DONE = "DONE";

    public static final int READS_ONE = 10;
    public static final int CHUNK_ONE = 2;
    public static final int READS_TWO = 30;
    public static final int CHUNK_TWO = 5;

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
            .instance(exec.getJobInstance())
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
            .instance(exec.getJobInstance())
            .execution(exec)
            .job(job)
            .build();
    }

    @SneakyThrows
    public AssertableJob oneStepReaderWriterJobEmptyParams() {
        val stats = ExecStats.builder().build();

        List<Long> data = LongStream.range(0, READS_ONE).boxed().collect(Collectors.toList());
        val job = jobBuilderFactory.get(READER_WRITER)
            .start(
                buildReaderWriterProcessorStep(STEP_ONE, stats, READS_ONE, CHUNK_ONE)
            )
            .build();

        val exec = jobRepository.createJobExecution(READER_WRITER, new JobParameters());

        return AssertableJob.builder()
            .stats(stats)
            .instance(exec.getJobInstance())
            .execution(exec)
            .job(job)
            .build();
    }

    @SneakyThrows
    public AssertableJob twoStepReaderWriterJobEmptyParams() {
        val stats = ExecStats.builder().build();
        val job = jobBuilderFactory.get(READER_WRITER)
            .start(
                buildReaderWriterProcessorStep(STEP_ONE, stats, READS_ONE, CHUNK_ONE)
            )
            .next(
                buildReaderWriterProcessorStep(STEP_TWO, stats, READS_TWO, CHUNK_TWO)
            )
            .build();

        val exec = jobRepository.createJobExecution(READER_WRITER, new JobParameters());

        return AssertableJob.builder()
            .stats(stats)
            .instance(exec.getJobInstance())
            .execution(exec)
            .job(job)
            .build();
    }

    private TaskletStep buildReaderWriterProcessorStep(String stepName, ExecStats stats, int reads, int chunk) {
        List<Long> data = LongStream.range(0, reads).boxed().collect(Collectors.toList());

        return stepBuilderFactory.get(stepName)
            .chunk(chunk)
            .reader(() -> {
                if (!data.isEmpty()) {
                    stats.getReads().incrementAndGet();
                    return data.remove(0);
                }
                return null;
            })
            .processor((Function<? super Object, ?>) in -> {
                stats.getProcesses().incrementAndGet();
                return in;
            })
            .writer(out -> {
                stats.getWrites().incrementAndGet();
                if (out.contains((long) reads - 1)) {
                    stats.getResult().set(DONE);
                }
            })
            .build();
    }
}
