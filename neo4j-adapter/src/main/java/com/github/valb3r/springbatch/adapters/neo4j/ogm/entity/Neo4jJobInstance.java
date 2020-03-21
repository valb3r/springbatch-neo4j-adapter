package com.github.valb3r.springbatch.adapters.neo4j.ogm.entity;

import com.github.valb3r.springbatch.adapters.neo4j.dao.converters.ParametersConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Version;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Collection;

import static com.github.valb3r.springbatch.adapters.neo4j.ogm.BatchRelationshipConst.PARENT;
import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@Getter
@Setter
@NodeEntity
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class Neo4jJobInstance {

    public static final Neo4jJobInstance.FromBatch MAP = Mappers.getMapper(Neo4jJobInstance.FromBatch.class);

    @Id
    @GeneratedValue
    private Long id;

    private String jobName;
    private String jobKey;

    @Convert(ParametersConverter.class)
    private JobParameters parameters;

    @Relationship(type = PARENT, direction = INCOMING)
    private Collection<Neo4jJobExecution> persistentJobExecutions;

    @CreatedDate
    private LocalDateTime createdAt;

    private Integer version;

    @Mapper
    public interface FromBatch {
        Neo4jJobInstance map(JobInstance batch);

        default JobInstance map(Neo4jJobInstance batch) {
            JobInstance instance = new JobInstance(batch.getId(), batch.getJobName());
            instance.setVersion(batch.getVersion());
            return instance;
        }
    }
}
