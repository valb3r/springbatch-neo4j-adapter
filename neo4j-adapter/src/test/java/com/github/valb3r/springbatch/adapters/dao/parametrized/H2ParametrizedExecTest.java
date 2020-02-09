package com.github.valb3r.springbatch.adapters.dao.parametrized;

import com.github.valb3r.springbatch.adapters.testconfig.h2.H2TestApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test-h2")
@Sql(scripts = "classpath:org/springframework/batch/core/schema-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "DROP ALL OBJECTS DELETE FILES", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(classes = H2TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class H2ParametrizedExecTest extends BaseParametrizedDaoBasedExecutionTest {
}
