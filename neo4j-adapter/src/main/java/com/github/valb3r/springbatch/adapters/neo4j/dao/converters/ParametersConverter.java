package com.github.valb3r.springbatch.adapters.neo4j.dao.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;
import org.neo4j.ogm.typeconversion.AttributeConverter;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.util.Date;
import java.util.Map;

public class ParametersConverter implements AttributeConverter<JobParameters, String> {

    private final ObjectMapper mapper = new ObjectMapper()
        .setDateFormat(new StdDateFormat());

    @Override
    @SneakyThrows
    public String toGraphProperty(JobParameters value) {
        if (null == value) {
            return null;
        }

        return mapper.writeValueAsString(value.getParameters());
    }

    @Override
    @SneakyThrows
    public JobParameters toEntityAttribute(String value) {
        if (null == value) {
            return null;
        }

        Map<String, ParseableParam> toParse =
            mapper.readerFor(new TypeReference<Map<String, ParseableParam>>() {}).readValue(value);
        JobParametersBuilder builder = new JobParametersBuilder();
        toParse.forEach((key, res) -> {
            val type = res.getType();
            val paramValue = res.getValue();

            if (type == JobParameter.ParameterType.DATE) {
                builder.addDate(key, readDate(paramValue));
            } else if (type == JobParameter.ParameterType.STRING) {
                builder.addString(key, (String) paramValue);
            } else if (type == JobParameter.ParameterType.DOUBLE) {
                builder.addDouble(key, castDouble(paramValue));
            } else if (type == JobParameter.ParameterType.LONG) {
                builder.addLong(key, castLong(paramValue));
            } else {
                throw new IllegalArgumentException("Unparseable object at: " + key);
            }
        });

        return builder.toJobParameters();
    }

    private Long castLong(Object param) {
        if (null == param) {
            return null;
        }

        if (param instanceof Integer) {
            return ((Integer) param).longValue();
        }

        return (Long) param;
    }

    private Double castDouble(Object param) {
        if (null == param) {
            return null;
        }

        if (param instanceof Float) {
            return ((Float) param).doubleValue();
        }

        return (Double) param;
    }

    @SneakyThrows
    private Date readDate(Object param) {
        if (null == param) {
            return null;
        }

        return mapper.readValue(String.format("[\"%s\"]", param), Date[].class)[0];
    }

    @Data
    private static class ParseableParam {

        private Object value;
        private JobParameter.ParameterType type;
        private boolean identifying;
    }
}
