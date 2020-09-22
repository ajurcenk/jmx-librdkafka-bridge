package org.ajur.demo.redis.app;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.*;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Simple MBean
 */
public class SimpleMBean {

    private static final String JMX_PREFIX = "test.metrics";

    final  JmxReporter reporter =  new JmxReporter(JMX_PREFIX);

    private static MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    private Metrics metrics = null;

    private Map<String, Double> metricValueMap = new ConcurrentHashMap<>();


    public static void main(String args[]) throws Exception {


        System.out.println("Starting application SimpleMBean");

        final LibRdKafkaClientCommonMBeanBuilder commonmBean = LibRdKafkaClientCommonMBeanBuilder.newInstance();

        StatJsonParser parserTets = new StatJsonParser();
        byte[] data = parserTets.readFileFromResources("stat.json");
        final Map<String,Object> statMap = parserTets.parseStat(data);

        /*final Map<String,Object> nestedMap = new HashMap<>();
        nestedMap.put("msg_size", 1000);
        statMap.put("nestedMap", nestedMap);*/


        final KafkaMetricsMapExtractor timeExt = KafkaMetricsMapExtractor.createNotMeasurableLongExtractor("time", statMap);

        final KafkaMetricsMapExtractor msgSizeExt = KafkaMetricsMapExtractor.createNotMeasurableLongExtractor("msg_size", statMap);
        final KafkaMetricsMapExtractor msgMaxSizeExt = KafkaMetricsMapExtractor.createNotMeasurableLongExtractor("msg_max", statMap);
        final KafkaMetricsMapExtractor txExt = KafkaMetricsMapExtractor.createNotMeasurableLongExtractor("tx", statMap);
        final KafkaMetricsMapExtractor txBytesExt = KafkaMetricsMapExtractor.createNotMeasurableLongExtractor("tx_bytes", statMap);
        final KafkaMetricsMapExtractor txMsgsExt = KafkaMetricsMapExtractor.createNotMeasurableLongExtractor("txmsgs", statMap);


        // Nested property
        final Object nestedObj = ((Map<String,Object>) statMap.get("brokers")).get("localhost:9092/2");


        final Map<String,Object> nestedMap = (Map<String,Object>) nestedObj;

        Supplier<Map<String,Object>> nestedMapSupplier = () -> {

            final Map<String,Object> brokerProp =  ((Map<String,Map<String,Object>>) statMap.get("brokers")).get("localhost:9092/2");

            final Map<String,Object> latencyMap =  (Map<String,Object>) brokerProp.get("int_latency");

            System.out.println(latencyMap);

            return brokerProp;
        };

                //() -> (Map<String,Object>) ((Map<String,Object>) statMap.get("brokers")).get("localhost:9092/2");

        final KafkaMetricsMapExtractor brokerStateExt = KafkaMetricsMapExtractor.createNotMeasurableStringExtractor("state", nestedMapSupplier);


        final Metrics metrics = commonmBean
                .addTag("client-id", "producer-1")
                .addTag("level", "top-level")

                .addMetric("time", "librdkafka-producer-metrics", "Wall clock time in seconds since the epoch", timeExt)

                .addMetric("msg_size", "librdkafka-producer-metrics", "Current total size of messages in producer queues", msgSizeExt)
                .addMetric("msg_max", "librdkafka-producer-metrics", "Threshold: maximum number of messages allowed allowed on the producer queues", msgMaxSizeExt)

                .addMetric("tx", "librdkafka-producer-metrics", "Total number of responses received from Kafka brokerss", txExt)
                .addMetric("tx_bytes", "librdkafka-producer-metrics", "Total number of bytes transmitted to Kafka brokers", txBytesExt)
                .addMetric("txmsgs", "librdkafka-producer-metrics", "Total number of messages transmitted (produced) to Kafka brokers", txMsgsExt)

                .addMetric("state", "librdkafka-producer-metrics", "Total number of messages transmitted (produced) to Kafka brokers", brokerStateExt)

                .build();

        final  JmxReporter reporter =  new JmxReporter(JMX_PREFIX);
        metrics.addReporter(reporter);

        // Check that bean: test.metrics:type=librdkafka-producer-metrics,client-id=producer-1,level=top-level exists

        final boolean isMbeanExists = server.isRegistered(new ObjectName("test.metrics:type=librdkafka-producer-metrics,client-id=producer-1,level=top-level"));

        System.out.println("isMbeanExists: " + isMbeanExists);


        Thread.sleep(1000 * 30 );
        System.out.println("Reload data...");
        data = parserTets.readFileFromResources("stat_1.json");
        final Map<String,Object>  statMap2 = parserTets.parseStat(data);
        statMap.putAll(statMap2);




        Thread.sleep(1000 * 60 * 10 );

        // Reload data simulation


        System.out.println("Stopping application SimpleMBean");

        metrics.close();
    }


    public static void simpleBeanTest() throws Exception {

        System.out.println("Starting application SimpleMBean");

        final SimpleMBean mbean = new SimpleMBean();

        mbean.init();


        int count = 50;

        while(count > 0) {


            mbean.updateMetrics();
            Thread.sleep(3000 );
            count--;
        }



        System.out.println("Stopping application SimpleMBean");
        mbean.destroy();
    }

    private void init() {


        metricValueMap.put("message-size", 10d);

        // Create test JMX metrics
        Map<String, String> metricTags = new LinkedHashMap<String, String>();
        metricTags.put("client-id", "producer-1");
        //metricTags.put("topic", "topic");

        MetricConfig metricConfig = new MetricConfig().tags(metricTags);
        this.metrics = new Metrics(metricConfig);


        // Add reporter
        this.metrics.addReporter(this.reporter);

        MetricName metricName = this.metrics.metricName("message-size", "not-java-producer-metrics", "message size");
        this.metrics.addMetric(metricName,(config,now) -> metricValueMap.get("message-size"));


        // Create a sensor
        //Sensor sensor = this.metrics.sensor("message-sizes");
        //sensor.record(10);

    }

    private void updateMetrics() {

        final Double newValue =  ThreadLocalRandom.current().nextDouble(10, 100);

        System.out.println("New metric value: " + newValue);
        this.metricValueMap.put("message-size", newValue);
    }

    private void destroy() {

        if (this.metrics != null) {

            metrics.close();
        }
    }

}
