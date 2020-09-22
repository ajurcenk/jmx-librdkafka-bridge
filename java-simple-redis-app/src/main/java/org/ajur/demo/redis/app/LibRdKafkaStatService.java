package org.ajur.demo.redis.app;

import com.sun.jmx.mbeanserver.JmxMBeanServer;
import org.ajur.demo.redis.app.config.ConfigService;
import org.apache.kafka.common.metrics.JmxReporter;

import javax.management.MBeanServer;
import java.util.Map;

/**
 * librdkafka stat. service (a single instance per application)
 */
public class LibRdKafkaStatService {

    private final ConfigService configSvc;

    // Stat. message parser
    final private LibRdKafkaStatMsgParser parser = new LibRdKafkaStatMsgParser();

    // Context factory
     private LibRdKafkaStatProcessorsContextFactory statProcessorsContextFactory;


    // Processor
    final private LibRdKafkaStatProcessor processor = new LibRdKafkaStatProcessor();

    protected LibRdKafkaStatService(ConfigService configSvc, MBeanServer mbeansServer) {

        this.configSvc = configSvc;
        this.statProcessorsContextFactory =  new LibRdKafkaStatProcessorsContextFactory(this.configSvc, mbeansServer);
    }

    public void collectStat(final CollectStatReq req) throws Exception {


        // TODO Check for empty message

        // Parse data
        final Map<String,Object> statMap = this.parser.parse(req.getJsonStatMsg().getBytes());

        // Create context
        final LibRdKafkaStatProcessorsContext ctx = statProcessorsContextFactory.create(statMap, req.getReporter());

        // Process data
        this.processor.processStatMessage(ctx);
    }


    public static class CollectStatReq {

        final private String jsonStatMsg;
        final private JmxReporter reporter;

        public CollectStatReq(String jsonStatMsg, JmxReporter reporter) {
            this.jsonStatMsg = jsonStatMsg;
            this.reporter = reporter;
        }

        public String getJsonStatMsg() {
            return jsonStatMsg;
        }

        public JmxReporter getReporter() {
            return reporter;
        }
    }

}
