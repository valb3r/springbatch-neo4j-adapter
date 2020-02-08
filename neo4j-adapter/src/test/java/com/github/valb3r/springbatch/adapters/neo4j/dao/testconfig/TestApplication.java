package com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig;

import com.github.valb3r.springbatch.adapters.neo4j.EnableSpringBatchNeo4jAdapter;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

@EnableBatchProcessing
@SpringBootApplication(
    scanBasePackages = "com.github.valb3r.springbatch.adapters.neo4j.dao.testconfig",
    // Spring-Batch includes this by default, disabling them
    exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
    }
)
@EnableSpringBatchNeo4jAdapter
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class);
    }
}
