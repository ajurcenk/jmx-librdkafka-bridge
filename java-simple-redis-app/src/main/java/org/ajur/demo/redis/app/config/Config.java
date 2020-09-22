package org.ajur.demo.redis.app.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private Map<StatCategoryLevel, StatCategory> categoriesMap = new HashMap<>();
    private StatCategory[] categories;
    private String jmxPrefix;


    public StatCategory[] getCategories() {
        return categories;
    }

    public void setCategories(StatCategory[] categories) {
        this.categories = categories;

        for (StatCategory cat: categories
             ) {

            categoriesMap.put(cat.getLevel(), cat);
        }
    }

    public String getJmxPrefix() {
        return jmxPrefix;
    }

    public void setJmxPrefix(String jmxPrefix) {
        this.jmxPrefix = jmxPrefix;
    }

    @JsonIgnore
    public Map<StatCategoryLevel, StatCategory> getCategoriesMap() {
        return categoriesMap;
    }

    @Override
    public String toString() {
        return "Config{" +
                "categories=" + Arrays.toString(categories) +
                '}';
    }
}
