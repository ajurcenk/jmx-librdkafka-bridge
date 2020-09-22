package org.ajur.demo.redis.app;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.Gauge;
import org.apache.kafka.common.metrics.MetricConfig;
import org.apache.kafka.common.metrics.Metrics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Not java kafka clients common MBean
 */
public class LibRdKafkaClientCommonMBeanBuilder {


    private Map<String, String> metricTags = new LinkedHashMap<String, String>();

    private List<MetricsExtractorMapper> extractorsMapsist = new ArrayList<>();

    private KafkaMetricsExtractor<?> extractor;

    LibRdKafkaClientCommonMBeanBuilder() {

    }



    public LibRdKafkaClientCommonMBeanBuilder addTag(final String tagName, final String tagValue) {

        this.metricTags.put(tagName, tagValue);
        return this;
    }

    public LibRdKafkaClientCommonMBeanBuilder tags(Map<String, String> tags) {

        this.metricTags.putAll(tags);
        return this;
    }


    public LibRdKafkaClientCommonMBeanBuilder addMetric(final String metricsName,
                                                        final String groupName,
                                                        String description,
                                                        final KafkaMetricsExtractor<?> extractor) {


        this.extractorsMapsist.add(new MetricsExtractorMapper(metricsName,groupName,description,extractor));

        return this;
    }



    public Metrics build() {

        final MetricConfig metricConfig = new MetricConfig().tags(metricTags);
        final Metrics metrics = new Metrics(metricConfig);

        // Add metrics
        this.extractorsMapsist.stream().forEach(item -> {

            final MetricName metricName = metrics.metricName(item.metricsName, item.groupName, item.description);

            if (item.extractor.isNotMeasurableMetric()) {

                if (item.extractor.getMetricsType().equals(Long.class)) {

                    metrics.addMetric(metricName, new Gauge<Object>() {
                        @Override
                        public Object value(MetricConfig config, long now) {
                            return item.extractor.extract();
                        }
                    });

                }
                else if  (item.extractor.getMetricsType().equals(String.class)) {

                    metrics.addMetric(metricName, new Gauge<String>() {
                        @Override
                        public String value(MetricConfig config, long now) {
                            return (String) item.extractor.extract();
                        }
                    });
                }

            }

            else {

                // Only double metrics supported for not measurable metric
                metrics.addMetric(metricName,(config,now) -> (Double) item.extractor.extract());
            }

        });

        return metrics;
    }



        public static LibRdKafkaClientCommonMBeanBuilder newInstance() {

        return  new LibRdKafkaClientCommonMBeanBuilder();
    }


    private static class MetricsExtractorMapper<T> {

        final String metricsName;
        final  String groupName;
        final String  description;

        final KafkaMetricsExtractor<T> extractor;

        public MetricsExtractorMapper(String metricsName, String groupName, String description, KafkaMetricsExtractor<T> extractor) {
            this.metricsName = metricsName;
            this.groupName = groupName;
            this.description = description;
            this.extractor = extractor;
        }
    }

}
