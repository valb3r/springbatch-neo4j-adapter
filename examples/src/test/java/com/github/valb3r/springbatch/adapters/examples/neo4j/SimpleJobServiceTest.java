package com.github.valb3r.springbatch.adapters.examples.neo4j;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SimpleJobServiceTest {

    @Autowired
    private SimpleJobService simpleJobService;

    @Autowired
    private JobInstanceDao instanceDao;

    @Autowired
    private JobLauncher launcher;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Test
    void runSimpleJob() {
        simpleJobService.runSimpleJob();

        assertThat(simpleJobService.getResult()).hasValue("Step TWO DONE");
        assertThat(instanceDao.getJobNames()).hasSize(1);
    }

    @Test
    void runSimpleJobThroughLauncher() throws Exception{
        AtomicReference<String> result = new AtomicReference<>();
        Job job = jobBuilderFactory.get("FOO")
                .start(stepBuilderFactory.get("ONE").tasklet((a, b) -> null).build())
                .next(stepBuilderFactory.get("TWO").tasklet((a, b) -> {
                    result.set("Step TWO DONE");
                    return null;
                }).build())
                .build();


        launcher.run(job, new JobParameters());

        assertThat(result).hasValue("Step TWO DONE");
        assertThat(instanceDao.getJobNames()).hasSize(1);
    }
}
