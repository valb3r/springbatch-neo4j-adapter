package com.github.valb3r.springbatch.adapters.testconfig.neo4j;

import com.github.valb3r.springbatch.adapters.neo4j.EnableSpringBatchNeo4jAdapter;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

@EnableBatchProcessing
@EnableSpringBatchNeo4jAdapter
@SpringBootApplication(
    scanBasePackages = {
        "com.github.valb3r.springbatch.adapters.testconfig.common",
        "com.github.valb3r.springbatch.adapters.testconfig.neo4j"
    },
    // Spring-Batch includes this by default, disabling them
    exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
    }
)
public class Neo4jTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(Neo4jTestApplication.class);
    }
}
