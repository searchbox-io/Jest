package io.searchbox.client;

import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.http.JestHttpClient;

import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.params.CoreConnectionPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

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
            HttpClient httpclient;
            client.setServers(httpClientConfig.getServerList());
            boolean isMultiThreaded = httpClientConfig.isMultiThreaded();
            if (isMultiThreaded) {
                PoolingClientConnectionManager cm = new PoolingClientConnectionManager();

                Integer maxTotal = httpClientConfig.getMaxTotalConnection();
                if (maxTotal != null) {
                    cm.setMaxTotal(maxTotal);
                }

                Integer defaultMaxPerRoute = httpClientConfig.getDefaultMaxTotalConnectionPerRoute();
                if (defaultMaxPerRoute != null) {
                    cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
                }

                Map<HttpRoute, Integer> maxPerRoute = httpClientConfig.getMaxTotalConnectionPerRoute();
                for (HttpRoute route : maxPerRoute.keySet()) {
                    cm.setMaxPerRoute(route, maxPerRoute.get(route));
                }
                httpclient = new DefaultHttpClient(cm);
                log.debug("Multi Threaded http client created");
            } else {
                httpclient = new DefaultHttpClient();
                log.debug("Default http client is created without multi threaded option");
            }

            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, httpClientConfig.getConnTimeout());
            httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,httpClientConfig.getReadTimeout());

            // set custom gson instance
            Gson gson = httpClientConfig.getGson();
            if (gson != null) {
                client.setGson(gson);
            }

            client.setHttpClient(httpclient);
            //set discovery (should be set after setting the httpClient on jestClient)
            if (httpClientConfig.isDiscoveryEnabled()) {
                log.info("Node Discovery Enabled...");
                NodeChecker nodeChecker = new NodeChecker(httpClientConfig, client);
                client.setNodeChecker(nodeChecker);
                nodeChecker.startAndWait();
            } else {
                log.info("Node Discovery Disabled...");
            }
        } else {
            log.debug("There is no configuration to create http client. Going to create simple client with default values");
            client.setHttpClient(new DefaultHttpClient());
            LinkedHashSet<String> servers = new LinkedHashSet<String>();
            servers.add("http://localhost:9200");
            client.setServers(servers);
        }


        try {
            client.setAsyncClient(new DefaultHttpAsyncClient());
        } catch (IOReactorException e) {
            log.error("Cannot set asynchronous http client to jest client. Exception occurred:" + e.getMessage());
        }

        return client;
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
