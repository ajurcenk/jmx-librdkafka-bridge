package org.ajur.demo.redis.app.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ConfigApp {


    public static void main(String[] args) throws JsonProcessingException {

        final List<StatProperty> propertiesList = new ArrayList<>();

        final StatProperty prop1 = new StatProperty();
        prop1.setName("test");
        prop1.setType(StatPropertyType.STRING);
        prop1.setDescription("test description");

        propertiesList.add(prop1);



        final StatCategory cat1 = new StatCategory();
        cat1.setLevel(StatCategoryLevel.TOP);
        cat1.setProperties( propertiesList.toArray(new StatProperty[propertiesList.size()]));


        final List<StatCategory> categories = new ArrayList<>();
        categories.add(cat1);

        final Config cfg = new Config();
        cfg.setCategories(categories.toArray(new StatCategory[0]));



        ObjectMapper mapper = new ObjectMapper();
        String jsonValue = mapper.writeValueAsString(cfg);

        System.out.println(jsonValue);
        System.out.println(mapper.readValue(jsonValue, Config.class).toString());

    }

}
