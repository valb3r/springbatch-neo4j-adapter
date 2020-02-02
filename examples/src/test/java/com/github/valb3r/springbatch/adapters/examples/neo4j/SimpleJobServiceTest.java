package com.github.valb3r.springbatch.adapters.examples.neo4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SimpleJobServiceTest {

    @Autowired
    private SimpleJobService simpleJobService;

    @Test
    void runSimpleJob() {
        simpleJobService.runSimpleJob();

        assertThat(simpleJobService.getResult()).hasValue("Step TWO DONE");
    }
}
