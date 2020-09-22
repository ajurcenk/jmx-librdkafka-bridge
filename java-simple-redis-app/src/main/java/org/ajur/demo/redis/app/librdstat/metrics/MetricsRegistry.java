package org.ajur.demo.redis.app.librdstat.metrics;

import org.apache.kafka.common.metrics.Metrics;

/**
 * Metrics registry
 */
public class MetricsRegistry {


    protected MetricsRegistry() {

    }

    public void addMetrics(final String beanName, final Metrics metrics) {

    }

    public Metrics getMetrics(final String beanName) {
        return null;
    }

    public boolean isBeanExists(final String beanName) {

        return false;
    }

    public static class MetricsRegistryBeansHolder {

        private final String beanName;
        private final Metrics metrics;

        protected MetricsRegistryBeansHolder(String beanName, Metrics metrics) {
            this.beanName = beanName;
            this.metrics = metrics;
        }

        public String getBeanName() {
            return beanName;
        }

        public Metrics getMetrics() {
            return metrics;
        }
    }

}


