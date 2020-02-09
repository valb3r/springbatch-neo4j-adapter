package com.github.valb3r.springbatch.adapters.testconfig.neo4j;

import com.github.valb3r.springbatch.adapters.testconfig.common.DbDropper;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class Neo4jConfig {

    @Bean
    DbDropper neo4jDropper(SessionFactory factory) {
        return () -> factory.openSession().query("MATCH (n) DETACH DELETE n", Collections.emptyMap());
    }
}
