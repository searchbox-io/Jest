package io.searchbox.client;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.config.idle.HttpReapableConnectionManager;
import io.searchbox.client.config.idle.IdleConnectionReaper;
import io.searchbox.client.http.JestHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.client.AuthCache;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * @author Dogukan Sonmez
 */
public class JestClientFactory {

    final static Logger log = LoggerFactory.getLogger(JestClientFactory.class);
    private HttpClientConfig httpClientConfig;

    public JestClient getObject() {
        JestHttpClient client = new JestHttpClient();

        if (httpClientConfig == null) {
            log.debug("There is no configuration to create http client. Going to create simple client with default values");
            httpClientConfig = new HttpClientConfig.Builder("http://localhost:9200").build();
        }

        client.setRequestCompressionEnabled(httpClientConfig.isRequestCompressionEnabled());
        client.setServers(httpClientConfig.getServerList());
        final HttpClientConnectionManager connectionManager = getConnectionManager();
        final NHttpClientConnectionManager asyncConnectionManager = getAsyncConnectionManager();
        client.setHttpClient(createHttpClient(connectionManager));
        client.setAsyncClient(createAsyncHttpClient(asyncConnectionManager));

        // set custom gson instance
        Gson gson = httpClientConfig.getGson();
        if (gson == null) {
            log.info("Using default GSON instance");
        } else {
            log.info("Using custom GSON instance");
            client.setGson(gson);
        }

        // set discovery (should be set after setting the httpClient on jestClient)
        if (httpClientConfig.isDiscoveryEnabled()) {
            log.info("Node Discovery enabled...");
            if (!Strings.isNullOrEmpty(httpClientConfig.getDiscoveryFilter())) {
                log.info("Node Discovery filtering nodes on \"{}\"", httpClientConfig.getDiscoveryFilter());
            }
            NodeChecker nodeChecker = createNodeChecker(client, httpClientConfig);
            client.setNodeChecker(nodeChecker);
            nodeChecker.startAsync();
            nodeChecker.awaitRunning();
        } else {
            log.info("Node Discovery disabled...");
        }

        // schedule idle connection reaping if configured
        if (httpClientConfig.getMaxConnectionIdleTime() > 0) {
            log.info("Idle connection reaping enabled...");

            IdleConnectionReaper reaper = new IdleConnectionReaper(httpClientConfig, new HttpReapableConnectionManager(connectionManager, asyncConnectionManager));
            client.setIdleConnectionReaper(reaper);
            reaper.startAsync();
            reaper.awaitRunning();
        } else {
            log.info("Idle connection reaping disabled...");
        }

        Set<HttpHost> preemptiveAuthTargetHosts = httpClientConfig.getPreemptiveAuthTargetHosts();
        if (!preemptiveAuthTargetHosts.isEmpty()) {
            log.info("Authentication cache set for preemptive authentication");
            client.setHttpClientContextTemplate(createPreemptiveAuthContext(preemptiveAuthTargetHosts));
        }

        client.setElasticsearchVersion(httpClientConfig.getElasticsearchVersion());

        return client;
    }

    public void setHttpClientConfig(HttpClientConfig httpClientConfig) {
        this.httpClientConfig = httpClientConfig;
    }

    private CloseableHttpClient createHttpClient(HttpClientConnectionManager connectionManager) {
        return configureHttpClient(
                HttpClients.custom()
                        .setConnectionManager(connectionManager)
                        .setDefaultRequestConfig(getRequestConfig())
                        .setProxyAuthenticationStrategy(httpClientConfig.getProxyAuthenticationStrategy())
                        .setRoutePlanner(getRoutePlanner())
                        .setDefaultCredentialsProvider(httpClientConfig.getCredentialsProvider())
        ).build();
    }

    private CloseableHttpAsyncClient createAsyncHttpClient(NHttpClientConnectionManager connectionManager) {
        return configureHttpClient(
                HttpAsyncClients.custom()
                        .setConnectionManager(connectionManager)
                        .setDefaultRequestConfig(getRequestConfig())
                        .setProxyAuthenticationStrategy(httpClientConfig.getProxyAuthenticationStrategy())
                        .setRoutePlanner(getRoutePlanner())
                        .setDefaultCredentialsProvider(httpClientConfig.getCredentialsProvider())
        ).build();
    }

    /**
     * Extension point
     * <p>
     * Example:
     * </p>
     * <pre>
     * final JestClientFactory factory = new JestClientFactory() {
     *    {@literal @Override}
     *  	protected HttpClientBuilder configureHttpClient(HttpClientBuilder builder) {
     *  		return builder.setDefaultHeaders(...);
     *    }
     * }
     * </pre>
     */
    protected HttpClientBuilder configureHttpClient(final HttpClientBuilder builder) {
        return builder;
    }

    /**
     * Extension point for async client
     */
    protected HttpAsyncClientBuilder configureHttpClient(final HttpAsyncClientBuilder builder) {
        return builder;
    }

    // Extension point
    protected HttpRoutePlanner getRoutePlanner() {
        return httpClientConfig.getHttpRoutePlanner();
    }

    // Extension point
    protected RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(httpClientConfig.getConnTimeout())
                .setSocketTimeout(httpClientConfig.getReadTimeout())
                .build();
    }

    // Extension point
    protected NHttpClientConnectionManager getAsyncConnectionManager() {
        PoolingNHttpClientConnectionManager retval;

        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setConnectTimeout(httpClientConfig.getConnTimeout())
                .setSoTimeout(httpClientConfig.getReadTimeout())
                .build();

        Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register("http", httpClientConfig.getHttpIOSessionStrategy())
                .register("https", httpClientConfig.getHttpsIOSessionStrategy())
                .build();

        try {
            retval = new PoolingNHttpClientConnectionManager(
                    new DefaultConnectingIOReactor(ioReactorConfig),
                    sessionStrategyRegistry
            );
        } catch (IOReactorException e) {
            throw new IllegalStateException(e);
        }

        final Integer maxTotal = httpClientConfig.getMaxTotalConnection();
        if (maxTotal != null) {
            retval.setMaxTotal(maxTotal);
        }
        final Integer defaultMaxPerRoute = httpClientConfig.getDefaultMaxTotalConnectionPerRoute();
        if (defaultMaxPerRoute != null) {
            retval.setDefaultMaxPerRoute(defaultMaxPerRoute);
        }
        final Map<HttpRoute, Integer> maxPerRoute = httpClientConfig.getMaxTotalConnectionPerRoute();
        for (Map.Entry<HttpRoute, Integer> entry : maxPerRoute.entrySet()) {
            retval.setMaxPerRoute(entry.getKey(), entry.getValue());
        }

        return retval;
    }

    // Extension point
    protected HttpClientConnectionManager getConnectionManager() {
        HttpClientConnectionManager retval;

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", httpClientConfig.getPlainSocketFactory())
                .register("https", httpClientConfig.getSslSocketFactory())
                .build();

        if (httpClientConfig.isMultiThreaded()) {
            log.info("Using multi thread/connection supporting pooling connection manager");
            final PoolingHttpClientConnectionManager poolingConnMgr = new PoolingHttpClientConnectionManager(registry);

            final Integer maxTotal = httpClientConfig.getMaxTotalConnection();
            if (maxTotal != null) {
                poolingConnMgr.setMaxTotal(maxTotal);
            }
            final Integer defaultMaxPerRoute = httpClientConfig.getDefaultMaxTotalConnectionPerRoute();
            if (defaultMaxPerRoute != null) {
                poolingConnMgr.setDefaultMaxPerRoute(defaultMaxPerRoute);
            }
            final Map<HttpRoute, Integer> maxPerRoute = httpClientConfig.getMaxTotalConnectionPerRoute();
            for (Map.Entry<HttpRoute, Integer> entry : maxPerRoute.entrySet()) {
                poolingConnMgr.setMaxPerRoute(entry.getKey(), entry.getValue());
            }
            retval = poolingConnMgr;
        } else {
            log.info("Using single thread/connection supporting basic connection manager");
            retval = new BasicHttpClientConnectionManager(registry);
        }

        return retval;
    }

    // Extension point
    protected NodeChecker createNodeChecker(JestHttpClient client, HttpClientConfig httpClientConfig) {
        return new NodeChecker(client, httpClientConfig);
    }

    // Extension point
    protected HttpClientContext createPreemptiveAuthContext(Set<HttpHost> targetHosts) {
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(httpClientConfig.getCredentialsProvider());
        context.setAuthCache(createBasicAuthCache(targetHosts));

        return context;
    }

    private AuthCache createBasicAuthCache(Set<HttpHost> targetHosts) {
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        for (HttpHost eachTargetHost : targetHosts) {
            authCache.put(eachTargetHost, basicAuth);
        }

        return authCache;
    }

}
