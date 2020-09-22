package org.ajur.demo.redis.app.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class StatCategory {


    private Map<String, StatProperty> propertiesMap = new LinkedHashMap<>();

    private StatCategoryLevel level;

    private StatProperty[] properties;

    public StatProperty[] getProperties() {
        return properties;
    }

    public void setProperties(StatProperty[] properties) {
        this.properties = properties;

        for (StatProperty prop : properties) {
            this.propertiesMap.put(prop.getName(), prop);
        }

    }

    public StatCategoryLevel getLevel() {
        return level;
    }

    public void setLevel(StatCategoryLevel level) {
        this.level = level;
    }

    @JsonIgnore
    public Map<String, StatProperty> getPropertiesMap() {
        return propertiesMap;
    }

    @Override
    public String toString() {
        return "StatCategory{" +
                "level=" + level +
                ", properties=" + Arrays.toString(properties) +
                '}';
    }
}
