package org.ajur.demo.redis.app.config;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatPropertyType {

    STRING("string"), INTEGER("int") ;

    private String name;

    StatPropertyType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "StatPropertyType{" +
                "name='" + name + '\'' +
                '}';
    }
}
