package io.searchbox.rs.client.config;

import java.util.Collection;

import io.searchbox.client.config.ClientConfig;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class RsClientConfig extends ClientConfig {


    public RsClientConfig(Builder builder) {
        super(builder);
    }

    public static class Builder extends ClientConfig.AbstractBuilder<RsClientConfig, Builder> {


        public Builder(RsClientConfig httpClientConfig) {
            super(httpClientConfig);
        }

        public Builder(Collection<String> serverUris) {
            super(serverUris);
        }

        public Builder(String serverUri) {
            super(serverUri);
        }

        public RsClientConfig build() {
            return new RsClientConfig(this);
        }

    }

}
