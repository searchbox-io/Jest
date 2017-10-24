package io.searchbox.client.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;

import java.net.ProxySelector;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private final HttpRoutePlanner httpRoutePlanner;
    private final AuthenticationStrategy proxyAuthenticationStrategy;
    private final SchemeIOSessionStrategy httpIOSessionStrategy;
    private final SchemeIOSessionStrategy httpsIOSessionStrategy;
    private Set<HttpHost> preemptiveAuthTargetHosts;
    private final ElasticsearchVersion elasticsearchVersion;

    public HttpClientConfig(Builder builder) {
        super(builder);
        this.maxTotalConnection = builder.maxTotalConnection;
        this.defaultMaxTotalConnectionPerRoute = builder.defaultMaxTotalConnectionPerRoute;
        this.maxTotalConnectionPerRoute = builder.maxTotalConnectionPerRoute;
        this.credentialsProvider = builder.credentialsProvider;
        this.sslSocketFactory = builder.sslSocketFactory;
        this.plainSocketFactory = builder.plainSocketFactory;
        this.httpRoutePlanner = builder.httpRoutePlanner;
        this.proxyAuthenticationStrategy = builder.proxyAuthenticationStrategy;
        this.httpIOSessionStrategy = builder.httpIOSessionStrategy;
        this.httpsIOSessionStrategy = builder.httpsIOSessionStrategy;
        this.preemptiveAuthTargetHosts = builder.preemptiveAuthTargetHosts;
        this.elasticsearchVersion = builder.elasticsearchVersion;
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

    public HttpRoutePlanner getHttpRoutePlanner() {
        return httpRoutePlanner;
    }

    public AuthenticationStrategy getProxyAuthenticationStrategy() {
        return proxyAuthenticationStrategy;
    }

    public SchemeIOSessionStrategy getHttpIOSessionStrategy() {
        return httpIOSessionStrategy;
    }

    public SchemeIOSessionStrategy getHttpsIOSessionStrategy() {
        return httpsIOSessionStrategy;
    }

    public Set<HttpHost> getPreemptiveAuthTargetHosts() {
        return preemptiveAuthTargetHosts;
    }

    public ElasticsearchVersion getElasticsearchVersion() {
        return elasticsearchVersion;
    }

    public static class Builder extends ClientConfig.AbstractBuilder<HttpClientConfig, Builder> {

        private Integer maxTotalConnection;
        private Integer defaultMaxTotalConnectionPerRoute;
        private Map<HttpRoute, Integer> maxTotalConnectionPerRoute = new HashMap<HttpRoute, Integer>();
        private CredentialsProvider credentialsProvider;
        private LayeredConnectionSocketFactory sslSocketFactory;
        private ConnectionSocketFactory plainSocketFactory;
        private HttpRoutePlanner httpRoutePlanner;
        private AuthenticationStrategy proxyAuthenticationStrategy;
        private SchemeIOSessionStrategy httpIOSessionStrategy;
        private SchemeIOSessionStrategy httpsIOSessionStrategy;
        private Set<HttpHost> preemptiveAuthTargetHosts = Collections.emptySet();
        private ElasticsearchVersion elasticsearchVersion = ElasticsearchVersion.UNKNOWN;

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
         * Sets the socket factory that will be used by <b>sync</b> client for HTTP scheme.
         * <p>
         * <code>SSLConnectionSocketFactory.getSocketFactory()</code> is used by default.
         * </p><p>
         * A bad example of trust-all socket factory creation can be done as below:
         * </p>
         * <pre>
         * // trust ALL certificates
         * SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
         *     public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
         *         return true;
         *     }
         * }).build();
         *
         * // skip hostname checks
         * HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
         *
         * SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
         * </pre>
         *
         * @param socketFactory socket factory instance that will be registered for <code>https</code> scheme.
         * @see SSLConnectionSocketFactory
         */
        public Builder sslSocketFactory(LayeredConnectionSocketFactory socketFactory) {
            this.sslSocketFactory = socketFactory;
            return this;
        }

        /**
         * Sets the socket factory that will be used by <b>sync</b> client for HTTPS scheme.
         * <p>
         * <code>PlainConnectionSocketFactory.getSocketFactory()</code> is used by default.
         * </p>
         *
         * @param socketFactory socket factory instance that will be registered for <code>http</code> scheme.
         * @see PlainConnectionSocketFactory
         */
        public Builder plainSocketFactory(ConnectionSocketFactory socketFactory) {
            this.plainSocketFactory = socketFactory;
            return this;
        }

        /**
         * Sets the socket factory that will be used by <b>async</b> client for HTTP scheme.
         * <p>
         * <code>NoopIOSessionStrategy.INSTANCE</code> is used by default.
         * </p>
         *
         * @param httpIOSessionStrategy SchemeIOSessionStrategy instance that will be registered for <code>http</code> scheme.
         * @see NoopIOSessionStrategy
         */
        public Builder httpIOSessionStrategy(SchemeIOSessionStrategy httpIOSessionStrategy) {
            this.httpIOSessionStrategy = httpIOSessionStrategy;
            return this;
        }

        /**
         * Sets the socket factory that will be used by <b>async</b> client for HTTPS scheme.
         * <p>
         * <code>SSLIOSessionStrategy.getSystemDefaultStrategy()</code> is used by default.
         * </p>
         *
         * @param httpsIOSessionStrategy SchemeIOSessionStrategy instance that will be registered for <code>https</code> scheme.
         * @see SSLIOSessionStrategy
         */
        public Builder httpsIOSessionStrategy(SchemeIOSessionStrategy httpsIOSessionStrategy) {
            this.httpsIOSessionStrategy = httpsIOSessionStrategy;
            return this;
        }

        /**
         * Sets preemptive authentication for the specified <b>target host</b> by pre-populating an authentication data cache.
         * <p>
         * It is mandatory to set a credentials provider to use preemptive authentication.
         * </p><p>
         * If preemptive authentication is set without setting a credentials provider an exception will be thrown.
         * </p>
         */
        public Builder setPreemptiveAuth(HttpHost targetHost) {
            return preemptiveAuthTargetHosts(Collections.singleton(targetHost));
        }

        /**
         * Sets preemptive authentication for the specified set of <b>target hosts</b> by pre-populating an authentication data cache.
         * <p>
         * It is mandatory to set a credentials provider to use preemptive authentication.
         * </p><p>
         * If preemptive authentication is set without setting a credentials provider an exception will be thrown.
         * </p>
         * @param preemptiveAuthTargetHosts set of hosts targeted for preemptive authentication
         */
        public Builder preemptiveAuthTargetHosts(Set<HttpHost> preemptiveAuthTargetHosts) {
            if (preemptiveAuthTargetHosts != null) {
                this.preemptiveAuthTargetHosts = new HashSet<HttpHost>(preemptiveAuthTargetHosts);
            }
            return this;
        }

        public Builder proxy(HttpHost proxy) {
            return proxy(proxy, null);
        }

        public Builder proxy(HttpHost proxy, AuthenticationStrategy proxyAuthenticationStrategy) {
            this.httpRoutePlanner = new DefaultProxyRoutePlanner(proxy);
            this.proxyAuthenticationStrategy = proxyAuthenticationStrategy;
            return this;
        }

        public Builder elasticsearchVersion(ElasticsearchVersion elasticsearchVersion) {
            this.elasticsearchVersion = elasticsearchVersion;
            return this;
        }

        public HttpClientConfig build() {
            // Lazily initialize if necessary, as the call can be expensive when done eagerly.
            if (this.sslSocketFactory == null) {
                this.sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
            }
            if(this.plainSocketFactory == null) {
                this.plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
            }
            if(this.httpRoutePlanner == null) {
                this.httpRoutePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
            }
            if(this.httpIOSessionStrategy == null) {
                this.httpIOSessionStrategy = NoopIOSessionStrategy.INSTANCE;
            }
            if(this.httpsIOSessionStrategy == null) {
                this.httpsIOSessionStrategy = SSLIOSessionStrategy.getSystemDefaultStrategy();
            }

            if (preemptiveAuthSetWithoutCredentials()) {
                throw new IllegalArgumentException("Preemptive authentication set without credentials provider");
            }

            return new HttpClientConfig(this);
        }

        private boolean preemptiveAuthSetWithoutCredentials() {
            return !preemptiveAuthTargetHosts.isEmpty() && credentialsProvider == null;
        }

    }

}
