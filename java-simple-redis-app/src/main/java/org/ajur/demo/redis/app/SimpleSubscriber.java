package org.ajur.demo.redis.app;


import org.apache.kafka.common.metrics.JmxReporter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Simple Redis subscriber
 */
public class SimpleSubscriber {


    LibRdKafkaStatServiceApp statApp;

    private static final String JEDIS_SERVER = "localhost";

    private Jedis jedis = null;


    public static void main(String[] args) throws Exception {


        final SimpleSubscriber sub = new SimpleSubscriber();

        System.out.println("Starting application SimpleSubscriber");

        try {

            sub.init();

            Thread.sleep(10000);

            System.out.println("Stopping application SimpleSubscriber");
        }
        finally {
            sub.destroy();
        }


    }


    private void init() throws Exception {

        this.jedis = new Jedis(JEDIS_SERVER);

        this.statApp = new LibRdKafkaStatServiceApp();
        statApp.init();

        this.jedis.subscribe(new StatSubscriber(statApp), "test");
    }

    private void destroy() {

        this.statApp.destroy();

        if (jedis != null) {
            jedis.close();
        }
    }


    private static class PubSubscriber extends JedisPubSub {

        @Override
        public void onMessage(String channel, String message) {

            System.out.println("Channel " + channel + " has sent a message : " + message );

        }
    }

    private static class StatSubscriber extends JedisPubSub {

        final private LibRdKafkaStatServiceApp statApp;


        public StatSubscriber(LibRdKafkaStatServiceApp statApp) {
            this.statApp = statApp;
        }

        @Override
        public void onMessage(String channel, String message)  {

            System.out.println("Channel " + channel + " has sent a message : " + message );

            final LibRdKafkaStatService.CollectStatReq req = new LibRdKafkaStatService.CollectStatReq(message, this.statApp.getReporter());

            try {

                statApp.getStatSvc().collectStat(req);

            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    }

}
