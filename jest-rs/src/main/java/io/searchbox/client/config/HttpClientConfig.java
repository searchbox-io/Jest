package io.searchbox.client.config;

import java.util.Collection;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class HttpClientConfig extends ClientConfig {


    public HttpClientConfig(Builder builder) {
        super(builder);
    }

    public static class Builder extends ClientConfig.AbstractBuilder<HttpClientConfig, Builder> {


        public Builder(HttpClientConfig httpClientConfig) {
            super(httpClientConfig);
        }

        public Builder(Collection<String> serverUris) {
            super(serverUris);
        }

        public Builder(String serverUri) {
            super(serverUri);
        }

        public HttpClientConfig build() {
            return new HttpClientConfig(this);
        }

    }

}
