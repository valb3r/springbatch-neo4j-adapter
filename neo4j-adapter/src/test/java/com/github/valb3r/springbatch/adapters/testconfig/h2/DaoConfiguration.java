package com.github.valb3r.springbatch.adapters.testconfig.h2;

import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.dao.DefaultExecutionContextSerializer;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DaoConfiguration {

    @Bean
    @SneakyThrows
    ExposedJobRepositoryFactoryBean exposedJobExplorerFactoryBean(
        DataSource dataSource, PlatformTransactionManager transactionManager
    ) {
        ExecutionContextSerializer serializer = new DefaultExecutionContextSerializer();
        ExposedJobRepositoryFactoryBean result = new ExposedJobRepositoryFactoryBean();
        result.setSerializer(serializer);
        result.setDataSource(dataSource);
        result.setTransactionManager(transactionManager);
        result.afterPropertiesSet();
        return result;
    }

    @Bean
    ExecutionContextDao executionContextDao(ExposedJobRepositoryFactoryBean factoryBean) {
        return factoryBean.pubCreateExecutionContextDao();
    }

    @Bean
    JobExecutionDao executionDao(ExposedJobRepositoryFactoryBean factoryBean) {
        return factoryBean.pubCreateJobExecutionDao();
    }

    @Bean
    JobInstanceDao instanceDao(ExposedJobRepositoryFactoryBean factoryBean) {
        return factoryBean.pubCreateJobInstanceDao();
    }

    @Bean
    StepExecutionDao stepExecutionDao(ExposedJobRepositoryFactoryBean factoryBean) {
        return factoryBean.pubCreateStepExecutionDao();
    }

    @Setter
    public static class ExposedJobRepositoryFactoryBean extends JobRepositoryFactoryBean {

        @SneakyThrows
        public ExecutionContextDao pubCreateExecutionContextDao() {
            return createExecutionContextDao();
        }

        @SneakyThrows
        public JobInstanceDao pubCreateJobInstanceDao() {
            return createJobInstanceDao();
        }

        @SneakyThrows
        public JobExecutionDao pubCreateJobExecutionDao() {
            return createJobExecutionDao();
        }

        @SneakyThrows
        public StepExecutionDao pubCreateStepExecutionDao() {
            return createStepExecutionDao();
        }
    }
}
