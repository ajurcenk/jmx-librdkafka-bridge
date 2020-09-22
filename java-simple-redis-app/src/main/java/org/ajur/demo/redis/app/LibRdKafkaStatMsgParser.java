package org.ajur.demo.redis.app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  Stat. message parser
 */
public class LibRdKafkaStatMsgParser {

    ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> parse(final byte[] statData) throws Exception {

        Map<String,Object> statMap = new ConcurrentHashMap<>();

        statMap = objectMapper.readValue(statData, ConcurrentHashMap.class);

        return statMap;
    }


}
