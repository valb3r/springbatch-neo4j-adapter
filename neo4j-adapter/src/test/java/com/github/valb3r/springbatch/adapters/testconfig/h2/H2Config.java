package com.github.valb3r.springbatch.adapters.testconfig.h2;

import com.github.valb3r.springbatch.adapters.testconfig.common.DbDropper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class H2Config {

    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
        return embeddedDatabaseBuilder
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }

    @Bean
    DbDropper h2Dropper() {
        return () -> {};
    }
}
