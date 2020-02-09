package com.github.valb3r.springbatch.adapters.testconfig.common.jobs;

import com.github.valb3r.springbatch.adapters.testconfig.common.Const;
import com.github.valb3r.springbatch.adapters.testconfig.common.dto.ExecStats;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@RequiredArgsConstructor
public class ParametrizedWriter implements ItemWriter<Integer> {

    private final int reads;
    private final ExecStats stats;

    @Override
    public void write(List<? extends Integer> items) {
        stats.getWrites().incrementAndGet();

        if (items.contains(reads - 1)) {
            stats.getResult().set(Const.DONE);
        }
    }
}
