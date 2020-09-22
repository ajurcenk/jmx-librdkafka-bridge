package org.ajur.demo.redis.app;

import org.ajur.demo.redis.app.config.*;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.JmxReporter;
import org.apache.kafka.common.metrics.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.ObjectName;
import java.util.*;
import java.util.function.Supplier;

/**
 * Librdkafka statistical callback processor
 */
public class LibRdKafkaStatProcessor {

    private static final Logger log = LoggerFactory.getLogger(LibRdKafkaStatProcessor.class);

    private Metrics firstBroker;

    protected LibRdKafkaStatProcessor() {
    }

    public void processStatMessage(final LibRdKafkaStatProcessorsContext ctx) throws Exception {

        log.debug("Entering to processStatMessage()");

        final Map<String, Object> topLevelMap = ctx.getStatMap();

        // Create top level MBean
        if (ctx.isNewClientId()) {

            log.debug("New client is detected. Creating new MBeans...");

            final String topLevelMbeanName = this.createMbeanName("top-level", ctx);
            if ( this.isMetricsMbeanExists(topLevelMbeanName, ctx) ) {

                log.warn("The new client Id is found, but Mbean exists: ", topLevelMbeanName, " Removing Mbean: ", topLevelMbeanName);

                // TODO Delete all MBeans by client Id
                ctx.getManageBeansServer().unregisterMBean(new ObjectName(topLevelMbeanName));

            }


           final Metrics topLevelMbean =  this.createTopLevelMBean(ctx, () -> topLevelMap);
           topLevelMbean.addReporter(ctx.getReporter());

           // Remove attribute
            final MetricName metricToRemove = topLevelMbean.metrics().values().stream().findFirst().get().metricName();


        } // if isNewClientId()



        // Create brokers MBeans
        final Map<String, Map<String, Object>> brokersProp =  (Map<String, Map<String, Object>>) topLevelMap.get("brokers");
        final List<String> brokersBeansName = new ArrayList<>();


        brokersProp.entrySet().stream().forEach(brokerEntry -> {

            try {

                final String brokerId = brokerEntry.getValue().get("nodeid").toString();
                final String brokenCommonBeanName = String.format("%s,broker-id=%s,broker-category=common", this.createMbeanName("broker", ctx), brokerId);
                final String brokenCommonBeanTags = String.format("client-id=%s,level=broker,broker-id=%s,broker-category=common", ctx.getClientId(), brokerId);
                brokersBeansName.add(brokenCommonBeanName);


                final Metrics brokerCommonMbean = new BrokerMBeanBuilderWrapper()
                        .level(StatCategoryLevel.BROKER)
                        .brokerId(brokerId)
                        .beanName(brokenCommonBeanName)
                        .tags(brokenCommonBeanTags)
                        .propMapExtractor(() -> ((Map<String, Map<String, Object>>) topLevelMap.get("brokers")).get(brokerEntry.getKey()))
                        .build(ctx);

                // Check if new Mbean is created
                if (brokerCommonMbean != null) {
                    firstBroker = brokerCommonMbean;
                }

                // Broker int_latency MBean (Internal producer queue latency in microseconds)
                final String brokenIntLatencyBeanName = String.format("%s,broker-id=%s,broker-category=int_latency",this.createMbeanName("broker", ctx), brokerId);
                final String brokenIntLatencyBeanTags = String.format("client-id=%s,level=broker,broker-id=%s,broker-category=int_latency", ctx.getClientId(),brokerId);
                brokersBeansName.add(brokenIntLatencyBeanName);


                final Metrics brokerIntLatencyCommonMbean = new BrokerMBeanBuilderWrapper()
                        .level(StatCategoryLevel.WINDOW_STATS)
                        .brokerId(brokerId)
                        .beanName(brokenIntLatencyBeanName)
                        .tags(brokenIntLatencyBeanTags)
                        .propMapExtractor( () ->  (  (Map<String,Object>) ( ((Map<String, Map<String, Object>>) topLevelMap.get("brokers")).get(brokerEntry.getKey())).get("int_latency"))  )
                        .build(ctx);

                // outbuf_latency
                final String brokerOutbufLatencyBeanName = String.format("%s,broker-id=%s,broker-category=outbuf_latency",this.createMbeanName("broker", ctx), brokerId);
                final String brokerOutbufLatencyBeanTags = String.format("client-id=%s,level=broker,broker-id=%s,broker-category=outbuf_latency", ctx.getClientId(),brokerId);
                brokersBeansName.add(brokerOutbufLatencyBeanName);

                final Metrics brokerOutbufLatencyCommonMbean = new BrokerMBeanBuilderWrapper()
                        .level(StatCategoryLevel.WINDOW_STATS)
                        .brokerId(brokerId)
                        .beanName(brokerOutbufLatencyBeanName)
                        .tags(brokerOutbufLatencyBeanTags)
                        .propMapExtractor( () ->  (  (Map<String,Object>) ( ((Map<String, Map<String, Object>>) topLevelMap.get("brokers")).get(brokerEntry.getKey())).get("outbuf_latency"))  )
                        .build(ctx);

                // rtt
                final String brokerRttLatencyBeanName = String.format("%s,broker-id=%s,broker-category=rtt",this.createMbeanName("broker", ctx), brokerId);
                final String brokerRttLatencyBeanTags = String.format("client-id=%s,level=broker,broker-id=%s,broker-category=rtt", ctx.getClientId(),brokerId);
                brokersBeansName.add(brokerRttLatencyBeanName);

                final Metrics brokerRttLatencyCommonMbean = new BrokerMBeanBuilderWrapper()
                        .level(StatCategoryLevel.WINDOW_STATS)
                        .brokerId(brokerId)
                        .beanName(brokerRttLatencyBeanName)
                        .tags(brokerRttLatencyBeanTags)
                        .propMapExtractor( () ->  (  (Map<String,Object>) ( ((Map<String, Map<String, Object>>) topLevelMap.get("brokers")).get(brokerEntry.getKey())).get("rtt"))  )
                        .build(ctx);


                // throttle
                final String brokerThrottleBeanName = String.format("%s,broker-id=%s,broker-category=throttle",this.createMbeanName("broker", ctx), brokerId);
                final String brokerThrottleBeanTags = String.format("client-id=%s,level=broker,broker-id=%s,broker-category=throttle", ctx.getClientId(),brokerId);
                brokersBeansName.add(brokerThrottleBeanName);

                final Metrics brokerThrottleCommonMbean = new BrokerMBeanBuilderWrapper()
                        .level(StatCategoryLevel.WINDOW_STATS)
                        .brokerId(brokerId)
                        .beanName(brokerThrottleBeanName)
                        .tags(brokerThrottleBeanTags)
                        .propMapExtractor( () ->  (  (Map<String,Object>) ( ((Map<String, Map<String, Object>>) topLevelMap.get("brokers")).get(brokerEntry.getKey())).get("throttle"))  )
                        .build(ctx);


            }
            catch (Exception brokerMbeansErr) {

                log.error("Error creating brokers MBeans", brokerMbeansErr);
                throw new RuntimeException(brokerMbeansErr);
            }

        });

        /*
        for (String beanName : brokersBeansName) {
            ctx.getManageBeansServer().unregisterMBean(new ObjectName(beanName));
        }*/


        log.debug("Exiting from processStatMessage()");

    }


    private Metrics createTopLevelMBean(final LibRdKafkaStatProcessorsContext ctx, final Supplier<Map<String,Object>> propsMapExtractor) throws Exception {

         // Create new MBean
        final LibRdKafkaClientCommonMBeanBuilder topLevelMBeanBuilder = LibRdKafkaClientCommonMBeanBuilder.newInstance();

        // Add tags
        topLevelMBeanBuilder
                // Tags
                .addTag("client-id", ctx.getClientId())
                .addTag("level", "top-level");


        // Add metrics
        final Map<String,Object> statMap = ctx.getStatMap();
        final Config cfg = ctx.getCfg();

        if (cfg == null) {

            throw new IllegalStateException("Configuration is not set");
        }

        final StatCategory category = cfg.getCategoriesMap().get(StatCategoryLevel.TOP);

        if (category == null) {

            throw new IllegalStateException("No configuration properties found  for level: " + StatCategoryLevel.TOP);
        }


        final String groupName = this.getMetricsGroupName(ctx);


        category.getPropertiesMap().values()
                .stream()
                .sorted((p1, p2) -> p1.compareTo(p2))
                .forEach(prop-> {


                    final KafkaMetricsMapExtractor extractor = getKafkaMetricsMapExtractor(propsMapExtractor, prop);

                    topLevelMBeanBuilder.addMetric(prop.getName(), groupName, prop.getDescription(), extractor);
        });

       final Metrics metrics = topLevelMBeanBuilder.build();

       return metrics;

    }

    private KafkaMetricsMapExtractor getKafkaMetricsMapExtractor(Supplier<Map<String, Object>> propMapExtractor, StatProperty prop) {

        return StatPropertyType.INTEGER.equals(prop.getType()) ?
                        KafkaMetricsMapExtractor.createNotMeasurableLongExtractor(prop.getName(), propMapExtractor) :
                        KafkaMetricsMapExtractor.createNotMeasurableStringExtractor(prop.getName(), propMapExtractor);
    }


    private Metrics createBrokerMBean(final StatCategoryLevel level, final String brokerId, final String brokenCommonBeanTags,
                                      final Supplier<Map<String,Object>> propMapExtractor, final LibRdKafkaStatProcessorsContext ctx) throws Exception {


        // Create new MBean
        final LibRdKafkaClientCommonMBeanBuilder mbeanBuilder = LibRdKafkaClientCommonMBeanBuilder.newInstance();

        // Parse tags
        Arrays.stream(brokenCommonBeanTags.split(",")).forEach(token-> {
            final String[]tagParts = token.split("=");
            mbeanBuilder.addTag(tagParts[0],tagParts[1]);

        });


        // Add metrics
        final Config cfg = ctx.getCfg();

        if (cfg == null) {

            throw new IllegalStateException("Configuration is not set");
        }

        final StatCategory category = cfg.getCategoriesMap().get(level);

        if (category == null) {

            throw new IllegalStateException("No configuration properties found  for level: " + level);
        }

        final String groupName = this.getMetricsGroupName(ctx);


        category.getPropertiesMap().values()
                .stream()
                .sorted((p1, p2) -> p1.compareTo(p2))
                .forEach(prop-> {


                    final KafkaMetricsMapExtractor extractor = getKafkaMetricsMapExtractor(propMapExtractor, prop);

                     mbeanBuilder.addMetric(prop.getName(), groupName, prop.getDescription(), extractor);
                });

        final Metrics metrics =  mbeanBuilder.build();

        return metrics;

    }

    private boolean isMetricsMbeanExists(final String beanName, final LibRdKafkaStatProcessorsContext ctx )
            throws Exception {

        log.debug("Entering to isMetricsMbeanExists()");


        log.debug("Looking for Mbean: {}",  beanName);

        final boolean isMbeanExists = ctx.getManageBeansServer().isRegistered(new ObjectName(beanName));

        log.debug("Exiting from isMetricsMbeanExists(). The bean: {} is {} ", beanName,  (isMbeanExists ? "found" : "not found"));

        return isMbeanExists;
    }


    private String createMbeanName(final String level,
                                         final LibRdKafkaStatProcessorsContext ctx)
            throws Exception {


        final String jmxPrefix = ctx.getCfg().getJmxPrefix();
        final String groupName =this.getMetricsGroupName(ctx);

        final String beanName = String.format("%s:type=%s,client-id=%s,level=%s", jmxPrefix, groupName,ctx.getClientId(), level );

        return beanName;
    }


    private String getMetricsGroupName(final LibRdKafkaStatProcessorsContext ctx) {

        final String groupName = String.format("librdkafka-%s-metrics", ctx.isProducerMetrics() ? "producer" : "unknown");

        return groupName;
    }

    /**
     * MBean builder wrapper
     */
    private class BrokerMBeanBuilderWrapper {

        private  StatCategoryLevel level;
        private  String brokerId;
        private  String tags;
        private String beanName;
        private Supplier<Map<String, Object>> propsMapExtractor;


        public BrokerMBeanBuilderWrapper level(StatCategoryLevel level) {

            this.level = level;

            return this;
        }

        public BrokerMBeanBuilderWrapper brokerId(String  brokerId) {

            this.brokerId = brokerId;

            return this;
        }

        public BrokerMBeanBuilderWrapper tags(String tags) {

            this.tags = tags;

            return this;
        }

        public BrokerMBeanBuilderWrapper beanName(String beanName) {

            this.beanName = beanName;

            return this;
        }

        public BrokerMBeanBuilderWrapper propMapExtractor(Supplier<Map<String, Object>> propsMapExtractor) {

            this.propsMapExtractor = propsMapExtractor;

            return this;
        }


        public Metrics build(final LibRdKafkaStatProcessorsContext ctx) throws Exception {

            // Create a mew bean if bean is not exists
            if (!LibRdKafkaStatProcessor.this.isMetricsMbeanExists(this.beanName, ctx)) {

                log.debug("Creating MBean {} for broker: {}", this.beanName, brokerId );

                final Metrics brokerCommonMbean = LibRdKafkaStatProcessor.
                        this.createBrokerMBean(this.level, this.brokerId, this.tags, this.propsMapExtractor, ctx);

                brokerCommonMbean.addReporter(ctx.getReporter());

                log.debug("MBean {} for broker: {} is created", this.beanName, brokerId );

                return brokerCommonMbean;
            }


            return null;
        }

    }

}
