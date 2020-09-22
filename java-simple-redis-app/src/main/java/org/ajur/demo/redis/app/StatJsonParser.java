package org.ajur.demo.redis.app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * STatistcs JSON parser
 */
public class StatJsonParser {

    ObjectMapper objectMapper = new ObjectMapper();


    public static void main(String args[]) throws Exception {


        StatJsonParser parserTets = new StatJsonParser();

        final byte[] data = parserTets.readFileFromResources("stat.json");

        final Map<String,Object> statMap = parserTets.parseStat(data);

        final KafkaMetricsMapExtractor clientIdExt = KafkaMetricsMapExtractor.createStringExtractor("client_id", statMap);
        final KafkaMetricsMapExtractor typeExt = KafkaMetricsMapExtractor.createStringExtractor("type", statMap);
        final KafkaMetricsMapExtractor tsExt = KafkaMetricsMapExtractor.createDoubleExtractor("ts", statMap);
        final KafkaMetricsMapExtractor tsMsgSize = KafkaMetricsMapExtractor.createDoubleExtractor("msg_size", statMap);
        final KafkaMetricsMapExtractor timeExt = KafkaMetricsMapExtractor.createDoubleExtractor("time", statMap);
        final KafkaMetricsMapExtractor timeNotMeasureExt = KafkaMetricsMapExtractor.createNotMeasurableLongExtractor("time", statMap);

        // msg_size

        System.out.println("client_id: " + clientIdExt.extract());
        System.out.println("type: : " + typeExt.extract());
        System.out.println("ts: " + tsExt.extract());
        System.out.println("msg_size : " + tsMsgSize.extract());
        System.out.println("time : " + timeExt.extract());
        System.out.println("time : " + timeNotMeasureExt.extract());
    }




    /**
     * Test simple parse operation
     *
     * @throws Exception
     */
    private static void testParese() throws Exception{



        StatJsonParser parserTets = new StatJsonParser();

        final byte[] data = parserTets.readFileFromResources("stat.json");

        final Map<String,Object> statMap = parserTets.parseStat(data);

        System.out.println(statMap);

        System.out.println("client.id: " + statMap.get("client_id"));
        System.out.println("brokers: " + statMap.get("brokers"));
    }


    public Map<String, Object> parseStat(final byte[] statData) throws Exception {

         Map<String,Object> statMap = new ConcurrentHashMap<>();

        statMap = objectMapper.readValue(statData, ConcurrentHashMap.class);

        return statMap;
    }



    public byte[] readFileFromResources(final String filename) throws Exception {

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try ( InputStream inputStream = classloader.getResourceAsStream(filename)) {

            final byte[] data = this.readBytes(inputStream);

            return  data;
        }

    }

    private byte[] readBytes(InputStream inputStream) throws IOException {

        byte[] b = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int c;
        while ((c = inputStream.read(b)) != -1) {
            os.write(b, 0, c);
        }
        return os.toByteArray();
    }

}
