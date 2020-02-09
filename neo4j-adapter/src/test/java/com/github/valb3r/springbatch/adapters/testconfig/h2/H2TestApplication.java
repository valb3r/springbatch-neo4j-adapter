package com.github.valb3r.springbatch.adapters.testconfig.h2;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration;

@EnableBatchProcessing
@SpringBootApplication(
    scanBasePackages = {
        "com.github.valb3r.springbatch.adapters.testconfig.common",
        "com.github.valb3r.springbatch.adapters.testconfig.h2"
    },
    // Disable Neo4j for H2 tests
    exclude = {
        Neo4jDataAutoConfiguration.class,
        Neo4jRepositoriesAutoConfiguration.class
    }
)
public class H2TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(H2TestApplication.class);
    }
}
