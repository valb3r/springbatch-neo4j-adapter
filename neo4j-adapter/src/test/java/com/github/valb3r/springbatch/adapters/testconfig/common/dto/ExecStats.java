package com.github.valb3r.springbatch.adapters.testconfig.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Builder
@AllArgsConstructor
public class ExecStats {

    @Builder.Default
    private final AtomicInteger reads = new AtomicInteger();

    @Builder.Default
    private final AtomicInteger processes = new AtomicInteger();

    @Builder.Default
    private final AtomicInteger writes = new AtomicInteger();

    @Builder.Default
    private final AtomicInteger taskletsDone = new AtomicInteger();

    @Builder.Default
    private final AtomicReference<String> result = new AtomicReference<>();
}
