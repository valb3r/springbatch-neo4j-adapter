package com.github.valb3r.springbatch.adapters.dao.repository;

import com.github.valb3r.springbatch.adapters.testconfig.common.DbDropper;
import com.github.valb3r.springbatch.adapters.testconfig.neo4j.Neo4jTestApplication;
import lombok.SneakyThrows;
import lombok.var;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Neo4jTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class JobInstanceDaoTest {

    private static final String JOB_NAME = "The job";
    private static final String JOB_NAME1 = "The job 1";
    private static final String JOB_NAME2 = "The job 2";

    @Autowired
    private JobInstanceDao instanceDao;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DbDropper dropper;

    @AfterEach
    void dropDatabase() {
        dropper.dropDatabase();
    }

    @Test
    void testCreateAndGetJobInstanceNoArgs() {
        var params = new JobParameters();
        var instance = instanceDao.createJobInstance(JOB_NAME, params);
        assertThat(instanceDao.getJobInstance(JOB_NAME, params)).isEqualTo(instance);
    }

    @Test
    void testCreateAndGetJobInstanceWithArgsNonIdentifiable() {
        var params = new JobParametersBuilder()
            .addDate("DATE", new Date())
            .addString("TEST", "VAL1")
            .toJobParameters();
        var instance = instanceDao.createJobInstance(JOB_NAME, params);
        assertThat(instanceDao.getJobInstance(JOB_NAME, params)).isEqualTo(instance);

        params = new JobParametersBuilder()
            .addDate("DATE", new Date())
            .addString("TEST", "VAL2")
            .toJobParameters();
        instance = instanceDao.createJobInstance(JOB_NAME, params);
        assertThat(instanceDao.getJobInstance(JOB_NAME, params)).isEqualTo(instance);
    }

    @Test
    void testCreateAndGetJobInstanceWithAllTypeArgsNonIdentifiable() {
        var params = new JobParametersBuilder()
            .addDate("DATE", new Date())
            .addLong("LONG", 1L)
            .addDouble("DOUBLE", 1.0)
            .addString("TEST", "VAL1")
            .toJobParameters();
        var instance = instanceDao.createJobInstance(JOB_NAME, params);
        assertThat(instanceDao.getJobInstance(JOB_NAME, params)).isEqualTo(instance);

        params = new JobParametersBuilder()
            .addDate("DATE", new Date())
            .addLong("LONG", 2L)
            .addDouble("DOUBLE", 2.0)
            .addString("TEST", "VAL2")
            .toJobParameters();
        instance = instanceDao.createJobInstance(JOB_NAME, params);
        assertThat(instanceDao.getJobInstance(JOB_NAME, params)).isEqualTo(instance);
    }

    @Test
    void testCreateAndGetJobInstanceWithArgsIdentifiable() {
        var params1 = new JobParametersBuilder()
            .addDate("DATE", new Date(), true)
            .addString("TEST", "VAL", true)
            .toJobParameters();
        var instance1 = instanceDao.createJobInstance(JOB_NAME, params1);

        var params2 = new JobParametersBuilder()
            .addDate("DATE", new Date(), true)
            .addString("TEST", "VAL1", true)
            .toJobParameters();
        var instance2 = instanceDao.createJobInstance(JOB_NAME, params2);

        assertThat(instanceDao.getJobInstance(JOB_NAME, params2)).isEqualTo(instance2);
        assertThat(instanceDao.getJobInstance(JOB_NAME, params1)).isEqualTo(instance1);
    }

    @Test
    void testCreateAndGetJobInstanceViaId() {
        var params = new JobParametersBuilder()
            .addDate("DATE", new Date())
            .addLong("LONG", 1L)
            .addDouble("DOUBLE", 1.0)
            .addString("TEST", "VAL1")
            .toJobParameters();
        var instance1 = instanceDao.createJobInstance(JOB_NAME, params);


        params = new JobParametersBuilder()
            .addDate("DATE", new Date())
            .addLong("LONG", 2L)
            .addDouble("DOUBLE", 2.0)
            .addString("TEST", "VAL2")
            .toJobParameters();
        var instance2 = instanceDao.createJobInstance(JOB_NAME, params);

        assertThat(instanceDao.getJobInstance(instance2.getInstanceId())).isEqualTo(instance2);
        assertThat(instanceDao.getJobInstance(instance1.getInstanceId())).isEqualTo(instance1);
    }

    @Test
    void testCreateAndGetJobInstanceViaExecution() {
        var instance1 = instanceDao.createJobInstance(JOB_NAME, new JobParameters());
        var execution1 = jobRepository.createJobExecution(instance1, new JobParameters(), "");

        var instance2 = instanceDao.createJobInstance(JOB_NAME, new JobParameters());
        var execution2 = jobRepository.createJobExecution(instance2, new JobParameters(), "");

        assertThat(instanceDao.getJobInstance(execution1)).isEqualTo(instance1);
        assertThat(instanceDao.getJobInstance(execution2)).isEqualTo(instance2);
    }

    @Test
    void testFindByNameJobInstances() {
        var params = new JobParameters();
        var instance1 = instanceDao.createJobInstance(JOB_NAME, params);
        var instance2 = instanceDao.createJobInstance(JOB_NAME1, params);
        var instance3 = instanceDao.createJobInstance(JOB_NAME2, params);

        assertThat(instanceDao.findJobInstancesByName(JOB_NAME, 0, 1)).containsOnly(instance1);
        assertThat(instanceDao.findJobInstancesByName(JOB_NAME1, 0, 3)).containsOnly(instance2);
        assertThat(instanceDao.findJobInstancesByName(JOB_NAME2, 0, 3)).containsOnly(instance3);
        assertThat(instanceDao.findJobInstancesByName(JOB_NAME, 0, 3))
            .containsExactly(instance1, instance2, instance3);
        assertThat(instanceDao.findJobInstancesByName(JOB_NAME, 2, 3)).containsOnly(instance3);
    }

    @Test
    void testGetByNameJobInstances() {
        var params = new JobParameters();
        var instance1 = instanceDao.createJobInstance(JOB_NAME, params);
        var instance2 = instanceDao.createJobInstance(JOB_NAME1, params);
        var instance3 = instanceDao.createJobInstance(JOB_NAME2, params);

        assertThat(instanceDao.getJobInstances(JOB_NAME, 0, 1)).containsOnly(instance1);
        assertThat(instanceDao.getJobInstances(JOB_NAME1, 0, 3)).containsOnly(instance2);
        assertThat(instanceDao.getJobInstances(JOB_NAME2, 0, 3)).containsOnly(instance3);
        assertThat(instanceDao.getJobInstances(JOB_NAME, 0, 3)).containsOnly(instance1);
    }

    @Test
    void testGetJobInstanceNames() {
        var params = new JobParameters();
        instanceDao.createJobInstance(JOB_NAME1, params);
        instanceDao.createJobInstance(JOB_NAME2, params);

        assertThat(instanceDao.getJobNames()).containsExactlyInAnyOrder(JOB_NAME1, JOB_NAME2);
    }

    @Test
    @SneakyThrows
    void testGetJobInstanceCount() {
        var params = new JobParameters();
        instanceDao.createJobInstance(JOB_NAME, params);
        instanceDao.createJobInstance(JOB_NAME, params);
        instanceDao.createJobInstance(JOB_NAME, params);

        assertThat(instanceDao.getJobInstanceCount(JOB_NAME)).isEqualTo(3);
    }
}
