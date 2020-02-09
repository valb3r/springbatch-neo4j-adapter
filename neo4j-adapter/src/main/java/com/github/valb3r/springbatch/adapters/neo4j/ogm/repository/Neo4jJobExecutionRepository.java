package com.github.valb3r.springbatch.adapters.neo4j.ogm.repository;

import com.github.valb3r.springbatch.adapters.neo4j.ogm.entity.Neo4jJobExecution;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.github.valb3r.springbatch.adapters.neo4j.ogm.BatchRelationshipConst.PARENT;

@Repository
public interface Neo4jJobExecutionRepository extends CrudRepository<Neo4jJobExecution, Long> {

    @Query("MATCH (s:Neo4jStepExecution)-[r1:" + PARENT + "]->(e:Neo4jJobExecution)-[r2:" + PARENT + "]->(i:Neo4jJobInstance) WHERE id(i) = $instanceId RETURN e, i, s, r1, r2")
    List<Neo4jJobExecution> findByJobInstanceId(long instanceId);

    @Query("MATCH (s:Neo4jStepExecution)-[r1:" + PARENT + "]->(e:Neo4jJobExecution)-[r2:" + PARENT + "]->(i:Neo4jJobInstance) WHERE id(i) = $instanceId RETURN e, i, s, r1, r2 " +
        "ORDER BY e.createTime DESC LIMIT 1")
    Optional<Neo4jJobExecution> findLatestExecution(@Param("instanceId") long instanceId);

    @Query("MATCH (s:Neo4jStepExecution)-[r1:" + PARENT + "]->(e:Neo4jJobExecution)-[r2:" + PARENT + "]->(i:Neo4jJobInstance) " +
        "WHERE i.jobName = $name AND e.startTime IS NOT NULL AND e.endTime IS NULL RETURN e, i, s, r1, r2 " +
        "ORDER BY id(e) DESC")
    List<Neo4jJobExecution> findRunningJobExecutions(@Param("name") String jobName);
}
