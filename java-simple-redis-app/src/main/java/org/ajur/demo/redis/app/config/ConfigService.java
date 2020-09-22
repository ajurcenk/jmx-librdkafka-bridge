package org.ajur.demo.redis.app.config;

public class ConfigService {

   final private Config config;

    public ConfigService(Config config) {
        this.config = config;
    }

    public Config getConfig() {

        return this.config;
    }


}
