package com.searchly.jestgae;

import com.google.appengine.api.urlfetch.FetchOptions;
import io.searchbox.client.config.ClientConfig;

import java.util.Collection;

/**
 * @author Scopewriter
 */
public class GaeClientConfig extends ClientConfig {

    private final FetchOptions fetchOptions;

    /**
     * There is no need for connection pool management etc. as GAE's URL Fetch Service manages it.
     * Only FetchOptions, which contains the request timeout, follow redirects etc. is configurable.
     * @param builder
     */
    public GaeClientConfig(Builder builder) {
        super(builder);
        this.fetchOptions = builder.fetchOptions;
    }

    public FetchOptions getFetchOptions() {
        return fetchOptions;
    }

    public static class Builder extends ClientConfig.AbstractBuilder<GaeClientConfig, Builder> {

        private FetchOptions fetchOptions;

        public Builder(GaeClientConfig httpClientConfig) {
            super(httpClientConfig);
        }

        public Builder(Collection<String> serverUris) {
            super(serverUris);
        }

        public Builder(String serverUri) {
            super(serverUri);
        }

        public Builder fetchOptions(FetchOptions fetchOptions) {
            this.fetchOptions = fetchOptions;
            return this;
        }

        public GaeClientConfig build() {
            return new GaeClientConfig(this);
        }

    }

}

