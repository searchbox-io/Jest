package io.searchbox.client.config;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class HttpClientConfig extends ClientConfig {

    private final Integer maxTotalConnection;
    private final Integer defaultMaxTotalConnectionPerRoute;
    private final Map<HttpRoute, Integer> maxTotalConnectionPerRoute;
    private final CredentialsProvider credentialsProvider;
    private final LayeredConnectionSocketFactory sslSocketFactory;
    private final ConnectionSocketFactory plainSocketFactory;

    public HttpClientConfig(Builder builder) {
        super(builder);
        this.maxTotalConnection = builder.maxTotalConnection;
        this.defaultMaxTotalConnectionPerRoute = builder.defaultMaxTotalConnectionPerRoute;
        this.maxTotalConnectionPerRoute = builder.maxTotalConnectionPerRoute;
        this.credentialsProvider = builder.credentialsProvider;
        this.sslSocketFactory = builder.sslSocketFactory;
        this.plainSocketFactory = builder.plainSocketFactory;
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

    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public LayeredConnectionSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public ConnectionSocketFactory getPlainSocketFactory() {
        return plainSocketFactory;
    }

    public static class Builder extends ClientConfig.AbstractBuilder<HttpClientConfig, Builder> {

        private Integer maxTotalConnection;
        private Integer defaultMaxTotalConnectionPerRoute;
        private Map<HttpRoute, Integer> maxTotalConnectionPerRoute = new HashMap<HttpRoute, Integer>();
        private CredentialsProvider credentialsProvider;
        private LayeredConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        private ConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();

        public Builder(HttpClientConfig httpClientConfig) {
            super(httpClientConfig);
            this.maxTotalConnection = httpClientConfig.maxTotalConnection;
            this.defaultMaxTotalConnectionPerRoute = httpClientConfig.defaultMaxTotalConnectionPerRoute;
            this.maxTotalConnectionPerRoute = httpClientConfig.maxTotalConnectionPerRoute;
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

        /**
         * Set a custom instance of an implementation of <code>CredentialsProvider</code>.
         * This method will override any previous credential setting (including <code>defaultCredentials</code>) on this builder instance.
         */
        public Builder credentialsProvider(CredentialsProvider credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        public Builder defaultCredentials(String username, String password) {
            this.credentialsProvider = new BasicCredentialsProvider();
            this.credentialsProvider.setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password)
            );
            return this;
        }

        /**
         *
         * @param socketFactory The socket factory instance that will be registered for <code>https</code> scheme.
         */
        public Builder sslSocketFactory(LayeredConnectionSocketFactory socketFactory) {
            this.sslSocketFactory = socketFactory;
            return this;
        }

        /**
         *
         * @param socketFactory The socket factory instance that will be registered for <code>http</code> scheme.
         */
        public Builder plainSocketFactory(ConnectionSocketFactory socketFactory) {
            this.plainSocketFactory = socketFactory;
            return this;
        }

        public HttpClientConfig build() {
            return new HttpClientConfig(this);
        }

    }

}
