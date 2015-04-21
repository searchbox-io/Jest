package io.searchbox.client;

import com.google.gson.Gson;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.config.idle.HttpReapableConnectionManager;
import io.searchbox.client.config.idle.IdleConnectionReaper;
import io.searchbox.client.http.JestHttpClient;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ProxySelector;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */
public class JestClientFactory {

    final static Logger log = LoggerFactory.getLogger(JestClientFactory.class);
    private HttpClientConfig httpClientConfig;

    public JestClient getObject() {
        JestHttpClient client = new JestHttpClient();

        if (httpClientConfig != null) {
            log.debug("Creating HTTP client based on configuration");
            client.setServers(httpClientConfig.getServerList());
            final HttpClientConnectionManager connectionManager = getConnectionManager();
            client.setHttpClient(createHttpClient(connectionManager));

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
                NodeChecker nodeChecker = new NodeChecker(httpClientConfig, client);
                client.setNodeChecker(nodeChecker);
                nodeChecker.startAsync();
                nodeChecker.awaitRunning();
            } else {
                log.info("Node Discovery disabled...");
            }

            // schedule idle connection reaping if configured
            if (httpClientConfig.getMaxConnectionIdleTime() > 0) {
                log.info("Idle connection reaping enabled...");

                IdleConnectionReaper reaper = new IdleConnectionReaper(httpClientConfig, new HttpReapableConnectionManager(connectionManager));
                client.setIdleConnectionReaper(reaper);
                reaper.startAsync();
                reaper.awaitRunning();
            } else {
                log.info("Idle connection reaping disabled...");
            }


        } else {
            log.debug("There is no configuration to create http client. Going to create simple client with default values");
            client.setHttpClient(HttpClients.createDefault());
            LinkedHashSet<String> servers = new LinkedHashSet<String>();
            servers.add("http://localhost:9200");
            client.setServers(servers);
        }

        client.setAsyncClient(HttpAsyncClients.custom().setRoutePlanner(getRoutePlanner()).build());
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
                        .setRoutePlanner(getRoutePlanner())
                        .setDefaultCredentialsProvider(getCredentialsProvider())
        ).build();
    }

    /**
     * Extension point
     * <p/>
     * Example:
     * <pre>
     * final JestClientFactory factory = new JestClientFactory() {
     *    {@literal @Override}
     *  	protected HttpClientBuilder configureHttpClient(HttpClientBuilder builder) {
     *  		return builder.setDefaultHeaders(...);
     *    }
     * }
     * </pre>
     *
     * @param builder
     * @return
     */
    protected HttpClientBuilder configureHttpClient(final HttpClientBuilder builder) {
        return builder;
    }

    // Extension point
    protected CredentialsProvider getCredentialsProvider() {
        return httpClientConfig.getCredentialsProvider();
    }

    // Extension point
    protected HttpRoutePlanner getRoutePlanner() {
        return new SystemDefaultRoutePlanner(ProxySelector.getDefault());
    }

    // Extension point
    protected RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(httpClientConfig.getConnTimeout())
                .setSocketTimeout(httpClientConfig.getReadTimeout())
                .build();
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
}
