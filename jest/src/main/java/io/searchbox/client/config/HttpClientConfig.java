package io.searchbox.client.config;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class HttpClientConfig extends ClientConfig {

    private Integer maxTotalConnection;
    private Integer defaultMaxTotalConnectionPerRoute;
    private Map<HttpRoute, Integer> maxTotalConnectionPerRoute;
    private HttpClientContext context;

    public HttpClientConfig(Builder builder) {
        super(builder);
        this.maxTotalConnection = builder.maxTotalConnection;
        this.defaultMaxTotalConnectionPerRoute = builder.defaultMaxTotalConnectionPerRoute;
        this.maxTotalConnectionPerRoute = builder.maxTotalConnectionPerRoute;
        this.context = builder.context;
    }

    public Map<HttpRoute, Integer> getMaxTotalConnectionPerRoute() {
        return maxTotalConnectionPerRoute;
    }

    public Integer getMaxTotalConnection() {
        return maxTotalConnection;
    }

    public Integer getDefaultMaxTotalConnectionPerRoute() {
        return defaultMaxTotalConnectionPerRoute;
    }

    public HttpClientContext getContext() {
        return context;
    }

    public static class Builder extends ClientConfig.AbstractBuilder<HttpClientConfig, Builder> {

        private Integer maxTotalConnection;
        private Integer defaultMaxTotalConnectionPerRoute;
        private Map<HttpRoute, Integer> maxTotalConnectionPerRoute = new HashMap<HttpRoute, Integer>();
        private HttpClientContext context;

        public Builder(HttpClientConfig httpClientConfig) {
            super(httpClientConfig);
            this.maxTotalConnection = httpClientConfig.maxTotalConnection;
            this.defaultMaxTotalConnectionPerRoute = httpClientConfig.defaultMaxTotalConnectionPerRoute;
            this.maxTotalConnectionPerRoute = httpClientConfig.maxTotalConnectionPerRoute;
            this.context = httpClientConfig.context;
        }

        public Builder(Collection<String> serverUris) {
            super(serverUris);
        }

        public Builder(String serverUri) {
            super(serverUri);
        }

        public Builder maxTotalConnection(int maxTotalConnection) {
            this.maxTotalConnection = maxTotalConnection;
            return this;
        }

        public Builder defaultMaxTotalConnectionPerRoute(int defaultMaxTotalConnectionPerRoute) {
            this.defaultMaxTotalConnectionPerRoute = defaultMaxTotalConnectionPerRoute;
            return this;
        }

        public Builder maxTotalConnectionPerRoute(Map<HttpRoute, Integer> maxTotalConnectionPerRoute) {
            this.maxTotalConnectionPerRoute.putAll(maxTotalConnectionPerRoute);
            return this;
        }

        public Builder maxTotalConnectionPerRoute(HttpRoute httpRoute, int maxTotalConnection) {
            this.maxTotalConnectionPerRoute.put(httpRoute, maxTotalConnection);
            return this;
        }

        public Builder context(HttpClientContext context) {
            this.context = context;
            return this;
        }

        public HttpClientConfig build() {
            return new HttpClientConfig(this);
        }

    }

}
