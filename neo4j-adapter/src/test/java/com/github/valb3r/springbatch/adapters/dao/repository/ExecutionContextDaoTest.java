package com.github.valb3r.springbatch.adapters.dao.repository;

import com.github.valb3r.springbatch.adapters.testconfig.common.DbDropper;
import com.github.valb3r.springbatch.adapters.testconfig.neo4j.Neo4jTestApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Neo4jTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ExecutionContextDaoTest {

    @Autowired
    private ExecutionContextDao ctxDao;

    @Autowired
    private DbDropper dropper;

    @AfterEach
    void dropDatabase() {
        dropper.dropDatabase();
    }

    @Test
    void getExecutionContextFromJobExecution() {
    }

    @Test
    void getExecutionContextFromStepExecution() {
    }

    @Test
    void saveExecutionContextForJobExecution() {
    }

    @Test
    void saveExecutionContextForStepExecution() {
    }

    @Test
    void saveExecutionContexts() {
    }

    @Test
    void updateExecutionContextForJobExecution() {

    }

    @Test
    void updateExecutionContextForStepExecution() {

    }
}
