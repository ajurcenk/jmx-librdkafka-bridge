package org.ajur.demo.redis.app;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Extracts Kafka metrics
 */
public abstract class KafkaMetricsExtractor<T> {



    private final Class<T> metricsType;
    private final String metricsName ;

    public boolean isNotMeasurableMetric() {
        return notMeasurableMetric;
    }

    private boolean notMeasurableMetric = false;


    protected KafkaMetricsExtractor(Class<T> metricsType, String metricsName, boolean notMeasurableMetric) {
        this(metricsType, metricsName);

        this.notMeasurableMetric = notMeasurableMetric;
    }


    protected KafkaMetricsExtractor(Class<T> metricsType, String metricsName) {
        this.metricsType = metricsType;
        this.metricsName = metricsName;
    }



    public Class<T> getMetricsType() {
        return metricsType;
    }

    public String getMetricsName() {
        return metricsName;
    }


    public abstract T extract();

}
