package org.ajur.demo.redis.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ajur.demo.redis.app.config.Config;
import org.ajur.demo.redis.app.config.ConfigService;
import org.apache.kafka.common.metrics.JmxReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;

public class LibRdKafkaStatServiceApp {


    private static final Logger log = LoggerFactory.getLogger(LibRdKafkaStatServiceApp.class);


    private JmxReporter reporter;
    private MBeanServer server;
    private LibRdKafkaStatService statSvc;

    private static final String JMX_PREFIX = "test.metrics";

    public static void main(String args[]) throws Exception {


       log.info("Starting  application LibRdKafkaStatServiceApp");

        final byte[] data = LibRdKafkaStatServiceApp.readFileFromResources("stat.json");

        final LibRdKafkaStatServiceApp app = new LibRdKafkaStatServiceApp();
        app.init();

        // Simulate multiple requests
        int reqCount = 1;

        while(reqCount <= 1) {

            System.out.println("Collecting request: " + reqCount);

            final byte[] data_json = LibRdKafkaStatServiceApp.readFileFromResources("stat_" + reqCount + ".json");
            final LibRdKafkaStatService.CollectStatReq svcReq = new LibRdKafkaStatService.CollectStatReq(new String(data_json), app.reporter);
            app.getStatSvc().collectStat(svcReq);

            reqCount++;

            Thread.sleep(1000 * 30);

            //app.getStatSvc().collectStat(svcReq);
        }


        Thread.sleep(1000 * 60 * 3 );

        log.debug("Stopping application LibRdKafkaStatServiceApp");

        // TODO Destroy MBeans
        app.destroy();


    }


  public static byte[] readFileFromResources(final String filename) throws Exception {

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try ( InputStream inputStream = classloader.getResourceAsStream(filename)) {

            final byte[] data = readBytes(inputStream);

            return  data;
        }

    }

    private static byte[] readBytes(InputStream inputStream) throws IOException {

        byte[] b = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int c;
        while ((c = inputStream.read(b)) != -1) {
            os.write(b, 0, c);
        }
        return os.toByteArray();
    }

    public void init() throws Exception {

        // Create configuration service
        final byte[] data = LibRdKafkaStatServiceApp.readFileFromResources("config.json");
        ObjectMapper mapper = new ObjectMapper();
        final Config cfg = mapper.readValue(new String(data), Config.class);
        final ConfigService cfgSvc = new ConfigService(cfg);


        this.server = ManagementFactory.getPlatformMBeanServer();
        this.reporter =  new JmxReporter(JMX_PREFIX);
        this.statSvc = new LibRdKafkaStatService(cfgSvc, this.server);

    }

    public void destroy() {

    }

    public LibRdKafkaStatService getStatSvc() {

        return statSvc;
    }

    public JmxReporter getReporter() {
        return reporter;
    }


}
