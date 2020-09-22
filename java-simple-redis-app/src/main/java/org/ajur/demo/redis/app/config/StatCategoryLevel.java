package org.ajur.demo.redis.app.config;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatCategoryLevel {

    TOP("top"), BROKER("broker"), WINDOW_STATS("window_stats") ;

    private String name;

    StatCategoryLevel(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "StatCategoryLevel{" +
                "name='" + name + '\'' +
                '}';
    }
}
