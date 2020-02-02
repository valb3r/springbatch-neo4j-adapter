package com.github.valb3r.springbatch.adapters.examples.neo4j;

import com.github.valb3r.springbatch.adapters.neo4j.EnableSpringBatchNeo4jAdapter;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

@EnableBatchProcessing
@SpringBootApplication(
    // Spring-Batch includes this by default, disabling them
    exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
    }
)
@EnableSpringBatchNeo4jAdapter
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class);
    }
}
