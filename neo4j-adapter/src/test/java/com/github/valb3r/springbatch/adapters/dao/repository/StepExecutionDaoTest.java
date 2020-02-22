package com.github.valb3r.springbatch.adapters.dao.repository;

import com.github.valb3r.springbatch.adapters.testconfig.common.DbDropper;
import com.github.valb3r.springbatch.adapters.testconfig.neo4j.Neo4jTestApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Neo4jTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class StepExecutionDaoTest {

    @Autowired
    private StepExecutionDao execDao;
    
    @Autowired
    private DbDropper dropper;

    @AfterEach
    void dropDatabase() {
        dropper.dropDatabase();
    }

    @Test
    void saveStepExecution() {
    }

    @Test
    void saveStepExecutions() {
    }

    @Test
    void updateStepExecution() {
    }

    @Test
    void getStepExecution() {
    }

    @Test
    void getLastStepExecution() {
    }

    @Test
    void addStepExecutions() {
    }
}
