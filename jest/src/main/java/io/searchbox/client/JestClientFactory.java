package io.searchbox.client;

import com.google.gson.Gson;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.http.JestHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
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
            client.setHttpClient(createHttpClient());

            // set custom gson instance
            Gson gson = httpClientConfig.getGson();
            if (gson != null) {
                client.setGson(gson);
            }

            //set discovery (should be set after setting the httpClient on jestClient)
            if (httpClientConfig.isDiscoveryEnabled()) {
                log.info("Node Discovery Enabled...");
                NodeChecker nodeChecker = new NodeChecker(httpClientConfig, client);
                client.setNodeChecker(nodeChecker);
                nodeChecker.startAsync();
                nodeChecker.awaitRunning();
            } else {
                log.info("Node Discovery Disabled...");
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

    private CloseableHttpClient createHttpClient() {
        return configureHttpClient(HttpClients.custom()
                .setConnectionManager(createConnectionManager())
                .setDefaultRequestConfig(createRequestConfig()))
                .setRoutePlanner(getRoutePlanner())
                .build();
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

    protected HttpRoutePlanner getRoutePlanner() {
        return new SystemDefaultRoutePlanner(ProxySelector.getDefault());
    }

    protected RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(httpClientConfig.getConnTimeout())
                .setSocketTimeout(httpClientConfig.getReadTimeout())
                .build();
    }

    protected HttpClientConnectionManager createConnectionManager() {
        if (httpClientConfig.isMultiThreaded()) {
            log.debug("Multi-threaded http connection manager created");
            final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            final Integer maxTotal = httpClientConfig.getMaxTotalConnection();
            if (maxTotal != null) {
                cm.setMaxTotal(maxTotal);
            }
            final Integer defaultMaxPerRoute = httpClientConfig.getDefaultMaxTotalConnectionPerRoute();
            if (defaultMaxPerRoute != null) {
                cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
            }
            final Map<HttpRoute, Integer> maxPerRoute = httpClientConfig.getMaxTotalConnectionPerRoute();
            for (HttpRoute route : maxPerRoute.keySet()) {
                cm.setMaxPerRoute(route, maxPerRoute.get(route));
            }
            return cm;
        }
        log.debug("Default http connection is created without multi threaded option");
        return new BasicHttpClientConnectionManager();
    }

    public Class<?> getObjectType() {
        return JestClient.class;
    }

    public boolean isSingleton() {
        return false;
    }

    public void setHttpClientConfig(HttpClientConfig httpClientConfig) {
        this.httpClientConfig = httpClientConfig;
    }
}
