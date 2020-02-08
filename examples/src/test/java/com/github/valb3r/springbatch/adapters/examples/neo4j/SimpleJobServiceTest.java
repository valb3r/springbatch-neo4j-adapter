package com.github.valb3r.springbatch.adapters.examples.neo4j;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class SimpleJobServiceTest {

    @Autowired
    private SimpleJobService simpleJobService;

    @Autowired
    private JobInstanceDao instanceDao;

    @Test
    void runSimpleJob() {
        simpleJobService.runSimpleJob();

        assertThat(simpleJobService.getResult()).hasValue("Step TWO DONE");
        assertThat(instanceDao.getJobNames()).hasSize(1);
    }
}
