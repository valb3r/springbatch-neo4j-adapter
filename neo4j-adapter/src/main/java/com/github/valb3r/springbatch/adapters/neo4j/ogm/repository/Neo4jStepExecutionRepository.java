package com.github.valb3r.springbatch.adapters.neo4j.ogm.repository;

import com.github.valb3r.springbatch.adapters.neo4j.ogm.entity.Neo4jStepExecution;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.github.valb3r.springbatch.adapters.neo4j.ogm.BatchRelationshipConst.PARENT;

@Repository
public interface Neo4jStepExecutionRepository extends CrudRepository<Neo4jStepExecution, Long> {

    @Query("MATCH (s:Neo4jStepExecution)-[r:" + PARENT + "]->(e:Neo4jJobExecution) " +
        "WHERE id(e) = $jobExecId AND id(s) = $stepExecId " +
        "RETURN s, e, r")
    Optional<Neo4jStepExecution> findBy(
        @Param("jobExecId") long jobExecId,
        @Param("stepExecId") long stepExecId
    );

    @Query("MATCH (s:Neo4jStepExecution)-[r1:" + PARENT + "]->(e:Neo4jJobExecution)-[r2:" + PARENT + "]->(j:Neo4jJobInstance) " +
        "WHERE id(j) = $jobExecInstanceId AND s.stepName = $stepName " +
        "RETURN s, e, j, r1, r2 ORDER BY s.startTime DESC, id(s) DESC")
    List<Neo4jStepExecution> findLastStepExecution(
        @Param("jobExecInstanceId") long jobExecInstanceId,
        @Param("stepName") String stepName
    );

    @Query("MATCH (s:Neo4jStepExecution)-[r1:" + PARENT + "]->(e:Neo4jJobExecution)-[r2:" + PARENT + "]->(j:Neo4jJobInstance) " +
        "WHERE id(e) = $jobExecId " +
        "RETURN s, e, j, r1, r2 ORDER BY id(s)")
    List<Neo4jStepExecution> findStepExecutions(@Param("jobExecId") long jobExecId);
}
