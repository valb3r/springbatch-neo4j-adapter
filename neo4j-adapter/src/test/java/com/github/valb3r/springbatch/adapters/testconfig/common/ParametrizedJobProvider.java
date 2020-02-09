package com.github.valb3r.springbatch.adapters.testconfig.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valb3r.springbatch.adapters.testconfig.common.dto.AssertableJob;
import com.github.valb3r.springbatch.adapters.testconfig.common.dto.ExecStats;
import com.github.valb3r.springbatch.adapters.testconfig.common.jobs.ParametrizedProcessor;
import com.github.valb3r.springbatch.adapters.testconfig.common.jobs.ParametrizedReader;
import com.github.valb3r.springbatch.adapters.testconfig.common.jobs.ParametrizedWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.ITEMS;
import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.READS_ONE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParametrizedJobProvider {

    public static final String TODAY = "TODAY";
    public static final String ONE = "ONE";
    public static final String TWO = "TWO";
    public static final Date EXPECTED_DATE =
        Date.from(LocalDateTime.parse("2000-01-01T00:00:00").toInstant(ZoneOffset.UTC));

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final JobRepository jobRepository;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @SneakyThrows
    public AssertableJob oneStepTaskletJobWithParams() {
        val stats = ExecStats.builder().build();

        val job = jobBuilderFactory.get(Const.ONE_STEP_TASKLET)
            .start(stepBuilderFactory.get(Const.STEP_ONE).tasklet((a, b) -> {
                log.info("TASKLET ONE");
                stats.getTaskletsDone().incrementAndGet();
                stats.getResult().set((String) b.getStepContext().getJobParameters().get(Const.DONE_PARAM));
                return null;
            }).build())
            .build();

        val exec = jobRepository.createJobExecution(Const.ONE_STEP_TASKLET, parametersBuilder().toJobParameters());

        return AssertableJob.builder()
            .stats(stats)
            .instance(exec.getJobInstance())
            .execution(exec)
            .job(job)
            .build();
    }

    @SneakyThrows
    public AssertableJob twoStepTaskletJobWithParams() {
        val stats = ExecStats.builder().build();

        val job = jobBuilderFactory.get(Const.TWO_STEPS_TASKLET)
            .start(stepBuilderFactory.get(Const.STEP_ONE).tasklet((a, b) -> {
                log.info("TASKLET ONE");
                stats.getTaskletsDone().incrementAndGet();
                b.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("PREV", Const.DONE);
                return null;
            }).build())
            .next(stepBuilderFactory.get(Const.STEP_TWO).tasklet((a, b) -> {
                log.info("TASKLET TWO");
                stats.getTaskletsDone().incrementAndGet();
                stats.getResult().set(
                    (String) b.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("PREV")
                );
                return null;
            }).build())
            .build();

        val exec = jobRepository.createJobExecution(Const.TWO_STEPS_TASKLET, parametersBuilder().toJobParameters());

        return AssertableJob.builder()
            .stats(stats)
            .instance(exec.getJobInstance())
            .execution(exec)
            .job(job)
            .build();
    }

    @SneakyThrows
    public AssertableJob oneStepReaderWriterJobWithParams() {
        val stats = ExecStats.builder().build();

        val job = jobBuilderFactory.get(Const.READER_WRITER)
            .start(
                buildParametrizedReaderWriterProcessorStep(Const.STEP_ONE, stats, Const.CHUNK_ONE)
            )
            .build();

        val exec = jobRepository.createJobExecution(
            Const.READER_WRITER,
            parametersBuilder().addString(
                ITEMS,
                MAPPER.writeValueAsString(LongStream.range(0, READS_ONE).boxed().collect(Collectors.toList()))
            ).toJobParameters()
        );

        return AssertableJob.builder()
            .stats(stats)
            .instance(exec.getJobInstance())
            .execution(exec)
            .job(job)
            .build();
    }

    private JobParametersBuilder parametersBuilder() {
        return new JobParametersBuilder()
            .addDate(TODAY, EXPECTED_DATE)
            .addDouble(ONE, 1.0)
            .addLong(TWO, 2L)
            .addString(Const.DONE_PARAM, Const.DONE);
    }

    private TaskletStep buildParametrizedReaderWriterProcessorStep(String stepName, ExecStats stats, int chunk) {
        return stepBuilderFactory.get(stepName)
            .<Long, Integer>chunk(chunk)
            .reader(new ParametrizedReader(MAPPER, stats))
            .processor(new ParametrizedProcessor(MAPPER, stats))
            .writer(new ParametrizedWriter(READS_ONE, stats))
            .build();
    }
}
