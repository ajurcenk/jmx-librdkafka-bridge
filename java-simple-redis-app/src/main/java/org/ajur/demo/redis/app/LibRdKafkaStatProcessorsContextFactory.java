package org.ajur.demo.redis.app;

import org.ajur.demo.redis.app.config.ConfigService;
import org.apache.kafka.common.metrics.JmxReporter;

import javax.management.MBeanServer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LibRdKafkaStatProcessorsContextFactory {

    // Stores the last data between all contexts
    private Map<String,ContextDataHolder> contextDataMap = new ConcurrentHashMap<>();
    private final MBeanServer mbeansServer;

    // Configuration service
    private final ConfigService configSvc;

    protected LibRdKafkaStatProcessorsContextFactory(final ConfigService configSvc, MBeanServer mbeansServer) {

        this.configSvc = configSvc;
        this.mbeansServer = mbeansServer;
    }

    public  LibRdKafkaStatProcessorsContext create(final Map<String,Object> statMap, final JmxReporter reporter) {

        // Get client Id
        final String clientId = statMap.getOrDefault("client_id", "").toString();

        // TODO Check client Id

        // Get context data by clint Id
        final boolean isContextDataExists = this.contextDataMap.containsKey(clientId);
        final boolean isNewClient = !isContextDataExists;

        if (isContextDataExists) {

            // Replace old context data
            this.contextDataMap.get(clientId).getData().putAll(statMap);
        }
        else  {

            // Create new context
            this.contextDataMap.put(clientId, new ContextDataHolder(clientId, statMap));
        }

        final LibRdKafkaStatProcessorsContext retVal = new LibRdKafkaStatProcessorsContext(reporter, isNewClient, clientId,
                statMap, this.configSvc.getConfig(), this.mbeansServer);

        return retVal;
    }


    /**
     * Context data holder
     */
    private static class ContextDataHolder {

        final private String clientId;
        final private Map<String,Object> data;

        public ContextDataHolder(String clientId, Map<String, Object> data) {
            this.clientId = clientId;
            this.data = data;
        }

        public String getClientId() {

            return clientId;
        }

        public Map<String, Object> getData() {

            return data;
        }
    }
}
