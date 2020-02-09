package com.github.valb3r.springbatch.adapters.testconfig.common;

@FunctionalInterface
public interface DbDropper {

    void dropDatabase();
}
