package com.github.valb3r.springbatch.adapters.neo4j.dao;

import com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig.JobProvider;
import com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig.TestApplication;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig.JobProvider.DONE;
import static com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig.JobProvider.ONE_STEP_TASKLET;
import static com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig.JobProvider.TWO_STEPS_TASKLET;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestApplication.class)
class ExecutionFlowValidationTest {

    @Autowired
    private JobProvider provider;

    @Autowired
    private JobInstanceDao instanceDao;

    @Autowired
    private SessionFactory factory;

    @AfterEach
    void dropDatabase() {
        factory.openSession().query("MATCH (n) DETACH DELETE n", Collections.emptyMap());
    }

    @Test
    void runOneStepTasklet() {
        val job = provider.oneStepTaskletJobEmptyParams();
        job.exec();

        assertThat(job.getStats().getResult()).hasValue(DONE);
        assertThat(job.getStats().getTaskletsDone()).hasValue(1);
        assertThat(instanceDao.getJobNames()).containsExactly(ONE_STEP_TASKLET);
    }

    @Test
    void runTwoStepsTasklet() {
        val job = provider.twoStepTaskletJobEmptyParams();
        job.exec();

        assertThat(job.getStats().getResult()).hasValue(DONE);
        assertThat(job.getStats().getTaskletsDone()).hasValue(2);
        assertThat(instanceDao.getJobNames()).containsExactly(TWO_STEPS_TASKLET);
    }

    @Test
    void runOneStepReaderWriter() {
    }

    @Test
    void runTwoStepsReaderWriter() {
    }

    @Test
    void runTwoStepsTaskletAndReaderWriter() {
    }
}
