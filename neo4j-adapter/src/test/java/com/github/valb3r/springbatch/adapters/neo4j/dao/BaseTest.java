package com.github.valb3r.springbatch.adapters.neo4j.dao;

import com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig.TestApplication;
import org.junit.jupiter.api.AfterEach;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

@ActiveProfiles("test")
@SpringBootTest(classes = TestApplication.class)
public abstract class BaseTest {

    @Autowired
    private SessionFactory factory;

    @AfterEach
    void dropDatabase() {
        factory.openSession().query("MATCH (n) DETACH DELETE n", Collections.emptyMap());
    }
}
