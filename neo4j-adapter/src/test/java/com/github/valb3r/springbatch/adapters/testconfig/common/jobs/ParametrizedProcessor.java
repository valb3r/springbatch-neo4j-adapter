package com.github.valb3r.springbatch.adapters.testconfig.common.jobs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valb3r.springbatch.adapters.testconfig.common.dto.ExecStats;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;

import static com.github.valb3r.springbatch.adapters.testconfig.common.Const.ITEMS;

@RequiredArgsConstructor
public class ParametrizedProcessor implements ItemProcessor<Long, Integer> {

    private final ObjectMapper mapper;
    private final ExecStats stats;

    private List<Long> processedItems;

    @Override
    public Integer process(Long item) {
        stats.getProcesses().incrementAndGet();
        processedItems.add(item);
        return item.intValue();
    }

    @BeforeStep
    @SneakyThrows
    void initialize(StepExecution stepExecution) {
        if (!stepExecution.getJobExecution().getExecutionContext().containsKey(ITEMS)) {
            processedItems = new ArrayList<>();
            return;
        }

        processedItems = new ArrayList<>(
            mapper.readValue(
                stepExecution.getJobExecution().getExecutionContext().getString(ITEMS),
                new TypeReference<List<Long>>() {})
        );
    }

    @AfterStep
    @SneakyThrows
    void persist(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put(ITEMS, mapper.writeValueAsString(processedItems));
    }
}
