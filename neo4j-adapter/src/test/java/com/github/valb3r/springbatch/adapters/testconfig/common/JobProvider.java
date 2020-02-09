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

    private final JobRepository jobRepository;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @SneakyThrows
    public AssertableJob oneStepTaskletJobEmptyParams() {
        val stats = ExecStats.builder().build();

        val job = jobBuilderFactory.get(Const.ONE_STEP_TASKLET)
            .start(stepBuilderFactory.get(Const.STEP_ONE).tasklet((a, b) -> {
                log.info("TASKLET ONE");
                stats.getTaskletsDone().incrementAndGet();
                stats.getResult().set(Const.DONE);
                return null;
            }).build())
            .build();

        val exec = jobRepository.createJobExecution(Const.ONE_STEP_TASKLET, new JobParameters());

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

        val job = jobBuilderFactory.get(Const.TWO_STEPS_TASKLET)
            .start(stepBuilderFactory.get(Const.STEP_ONE).tasklet((a, b) -> {
                log.info("TASKLET ONE");
                stats.getTaskletsDone().incrementAndGet();
                return null;
            }).build())
            .next(stepBuilderFactory.get(Const.STEP_TWO).tasklet((a, b) -> {
                log.info("TASKLET TWO");
                stats.getTaskletsDone().incrementAndGet();
                stats.getResult().set(Const.DONE);
                return null;
            }).build())
            .build();

        val exec = jobRepository.createJobExecution(Const.TWO_STEPS_TASKLET, new JobParameters());

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
        val job = jobBuilderFactory.get(Const.READER_WRITER)
            .start(
                buildReaderWriterProcessorStep(Const.STEP_ONE, stats, Const.READS_ONE, Const.CHUNK_ONE)
            )
            .build();

        val exec = jobRepository.createJobExecution(Const.READER_WRITER, new JobParameters());

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
        val job = jobBuilderFactory.get(Const.READER_WRITER)
            .start(
                buildReaderWriterProcessorStep(Const.STEP_ONE, stats, Const.READS_ONE, Const.CHUNK_ONE)
            )
            .next(
                buildReaderWriterProcessorStep(Const.STEP_TWO, stats, Const.READS_TWO, Const.CHUNK_TWO)
            )
            .build();

        val exec = jobRepository.createJobExecution(Const.READER_WRITER, new JobParameters());

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
                    stats.getResult().set(Const.DONE);
                }
            })
            .build();
    }
}
