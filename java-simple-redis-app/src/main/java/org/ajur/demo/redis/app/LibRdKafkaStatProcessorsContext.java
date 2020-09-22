package org.ajur.demo.redis.app;

import org.ajur.demo.redis.app.config.Config;
import org.apache.kafka.common.metrics.JmxReporter;

import javax.management.MBeanServer;
import java.util.Map;

/**
 * LibRdKafka stat. processor context, contains client id and data as map
 */
public class LibRdKafkaStatProcessorsContext {


    final private JmxReporter reporter;


    final private  MBeanServer manageBeansServer;

    final private boolean newClientId;
    final private String clientId;
    final private Map<String,Object> statMap;

    private boolean isProducerMetrics = false;


    final private Config cfg;


    public LibRdKafkaStatProcessorsContext(JmxReporter reporter, boolean newClientId, String clientId, Map<String,
            Object> statMap, Config cfg, final  MBeanServer server) {
        this.reporter = reporter;
        this.newClientId = newClientId;
        this.clientId = clientId;
        this.statMap = statMap;
        this.cfg = cfg;
        this.manageBeansServer = server;

        this.isProducerMetrics = "producer".equals(statMap.getOrDefault("type", ""));
    }

    public String getClientId() {

        return clientId;
    }

    public Map<String, Object> getStatMap() {

        return statMap;
    }

    public boolean isNewClientId() {

        return newClientId;
    }

    public JmxReporter getReporter() {

        return reporter;
    }

    public Config getCfg() {

        return cfg;
    }

    public boolean isProducerMetrics() {

        return isProducerMetrics;
    }

    public MBeanServer getManageBeansServer() {
        return manageBeansServer;
    }



}
