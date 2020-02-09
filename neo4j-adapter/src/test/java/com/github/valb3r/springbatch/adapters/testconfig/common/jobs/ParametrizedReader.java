package com.github.valb3r.springbatch.adapters.testconfig.common.jobs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valb3r.springbatch.adapters.testconfig.common.dto.ExecStats;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;

import java.util.List;

import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.ITEMS;

@RequiredArgsConstructor
public class ParametrizedReader implements ItemReader<Long> {

    private final ObjectMapper mapper;
    private final ExecStats stats;

    private List<Long> items;

    @Override
    public Long read() {
        if (items.isEmpty()) {
            return null;
        }

        stats.getReads().incrementAndGet();
        return items.remove(0);
    }

    @BeforeStep
    @SneakyThrows
    void initialize(StepExecution stepExecution) {
        items = mapper.readValue(
            stepExecution.getJobExecution().getJobParameters().getString(ITEMS),
            new TypeReference<List<Long>>() {}
        );
    }
}
