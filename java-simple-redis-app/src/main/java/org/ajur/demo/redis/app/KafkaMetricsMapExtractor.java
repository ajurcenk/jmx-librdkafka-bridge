package org.ajur.demo.redis.app;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KafkaMetricsMapExtractor<T> extends KafkaMetricsExtractor<T> {


    /**
     * Properties maps extractor
     */
    private final Supplier<Map<String,Object>> propertiesExtractor;


    // Converters
    private static final Map<String, Method> converters = new HashMap<>();

    static {
        Method[] methods = KafkaMetricsMapExtractor.class.getDeclaredMethods();
        for (Method method: methods) {
            if (method.getName().startsWith("convertFrom")) {
                converters.put(method.getName(), method);
            }
        }
    }

    // Metrics data
    private final Map<String,Object> data;

    protected KafkaMetricsMapExtractor(Class<T> metricsType, String metricsName, Map<String,Object> data) {

        super(metricsType, metricsName);

        this.data = data;
        this.propertiesExtractor = () -> this.data;
    }


    protected KafkaMetricsMapExtractor(Class<T> metricsType, String metricsName, boolean notMeasurableMetric, Map<String,Object> data) {

        super(metricsType, metricsName, notMeasurableMetric);

        this.data = data;
        this.propertiesExtractor = () -> this.data;
    }

    protected KafkaMetricsMapExtractor(Class<T> metricsType, String metricsName, boolean notMeasurableMetric,
                                       Supplier<Map<String,Object>> propertiesExtractor) {

        super(metricsType, metricsName, notMeasurableMetric);

        this.propertiesExtractor = propertiesExtractor;
        this.data = null;
    }

    @Override
    public T extract() {

         Object valueOrigin = this.propertiesExtractor.get().get(this.getMetricsName());

        // TODO Check for null

        if (this.getMetricsType().isAssignableFrom(valueOrigin.getClass())) {
            return this.getMetricsType().cast(valueOrigin);
        }

        String fromType = valueOrigin.getClass().getSimpleName();
        String toType = getMetricsType().getSimpleName();

        String converterName = String.format("convertFrom%sTo%s", fromType, toType);
        Method converter = converters.get(converterName);

        if (converter == null) {
            String message = String.format("Can't convert from `%s` to `%s`. Not found satisfied conversion method.", fromType, toType);
            throw new UnsupportedOperationException(message);
        }

        try {
           return this.getMetricsType().cast(converter.invoke(this.getMetricsType(), valueOrigin));
        } catch (Exception e) {
            String message = String.format("Can't convert from `%s` to `%s`. Conversion aborted with error: %s", fromType, toType, e.getMessage());
            throw new RuntimeException(message, e);
        }

    }


    // Convert from String
    private static Integer convertFromStringToInteger(String value) {
        return Integer.valueOf(value);
    }

    private static Long convertFromStringToLong(String value) {
        return Long.valueOf(value);
    }

    private static Float convertFromStringToFloat(String value) {
        return Float.valueOf(value);
    }

    private static Double convertFromStringToDouble(String value) {
        return Double.valueOf(value);
    }

    private static Boolean convertFromStringToBoolean(String value) {
        return Boolean.valueOf(value);
    }

    private static BigInteger convertFromStringToBigInteger(String value) {
        return new BigInteger(value);
    }

    private static BigDecimal convertFromStringToBigDecimal(String value) {
        return new BigDecimal(value);
    }

    // Convert from Long

    private static String convertFromLongToString(Long value) {
        return String.valueOf(value);
    }

    private static Integer convertFromLongToInteger(Long value) {
        return value.intValue();
    }

    private static Float convertFromLongToFloat(Long value) {
        return Float.valueOf(value);
    }

    private static Double convertFromLongToDouble(Long value) {
        return Double.valueOf(value);
    }

    private static Boolean convertFromLongToBoolean(Long value) {
        return value != 0;
    }

    private static BigInteger convertFromLongToBigInteger(Long value) {
        return BigInteger.valueOf(value);
    }

    private static BigDecimal convertFromLongToBigDecimal(Long value) {
        return BigDecimal.valueOf(value);
    }



    // Converter from Integer

    private static String convertFromIntegerToString(Integer value) {
        return String.valueOf(value);
    }

    private static Long convertFromIntegerToLong(Integer value) {
        return Long.valueOf(value);
    }

    private static Float convertFromIntegerToFloat(Integer value) {
        return Float.valueOf(value);
    }

    private static Double convertFromIntegerToDouble(Integer value) {
        return Double.valueOf(value);
    }

    private static Boolean convertFromIntegerToBoolean(Integer value) {
        return value != 0;
    }

    private static BigInteger convertFromIntegerToBigInteger(Integer value) {
        return BigInteger.valueOf(value);
    }

    private static BigDecimal convertFromIntegerToBigDecimal(Integer value) {
        return BigDecimal.valueOf(value);
    }
    public static <T> T convert(Object from, Class<T> toClass) {

        if (from == null) {
            return null;
        }

        if (toClass.isAssignableFrom(from.getClass())) {
            return toClass.cast(from);
        }

        String fromType = from.getClass().getSimpleName();
        String toType = toClass.getSimpleName();

        String converterName = String.format("convertFrom%sTo%s", fromType, toType);
        Method converter = converters.get(converterName);

        if (converter == null) {
            String message = String.format("Can't convert from `%s` to `%s`. Not found satisfied conversion method.", fromType, toType);
            throw new UnsupportedOperationException(message);
        }

        try {
            return toClass.cast(converter.invoke(toClass, from));
        } catch (Exception e) {
            String message = String.format("Can't convert from `%s` to `%s`. Conversion aborted with error: %s", fromType, toType, e.getMessage());
            throw new RuntimeException(message, e);
        }
    }


    public static KafkaMetricsMapExtractor<String> createStringExtractor(final String metricsName, Map<String,Object> data  ) {

        return new KafkaMetricsMapExtractor<String>(String.class, metricsName, data);
    }

    public static KafkaMetricsMapExtractor<Double> createDoubleExtractor(final String metricsName, Map<String,Object> data  ) {

        return new KafkaMetricsMapExtractor<Double>(Double.class, metricsName, data);
    }


    public static KafkaMetricsMapExtractor<Long> createNotMeasurableLongExtractor(final String metricsName, Map<String,Object> data  ) {

        return new KafkaMetricsMapExtractor<Long>(Long.class, metricsName, true, data);
    }

    public static KafkaMetricsMapExtractor<Long> createNotMeasurableLongExtractor(final String metricsName, Supplier<Map<String,Object>> propsExtractor  ) {

        return new KafkaMetricsMapExtractor<Long>(Long.class, metricsName, true, propsExtractor);
    }


    public static KafkaMetricsMapExtractor<String> createNotMeasurableStringExtractor(final String metricsName, Map<String,Object> data  ) {

        return new KafkaMetricsMapExtractor<String>(String.class, metricsName, true, data);
    }

    public static KafkaMetricsMapExtractor<String> createNotMeasurableStringExtractor(final String metricsName,
                                                                                      Supplier<Map<String,Object>> propsExtractor) {

        return new KafkaMetricsMapExtractor<String>(String.class, metricsName, true, propsExtractor);
    }


}
