package io.searchbox.client;

import com.google.gson.Gson;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.http.JestHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class JestClientFactory {

    final static Logger log = LoggerFactory.getLogger(JestClientFactory.class);
    private ClientConfig clientConfig;

    public JestClient getObject() {
        JestHttpClient client = new JestHttpClient();
        HttpClient httpclient;

        if (clientConfig != null) {
            log.debug("Creating HTTP client based on configuration");
            client.setServers(clientConfig.getServerList());
            boolean isMultiThreaded = clientConfig.isMultiThreaded();
            if (isMultiThreaded) {
                PoolingClientConnectionManager cm = new PoolingClientConnectionManager();

                Integer maxTotal = clientConfig.getMaxTotalConnection();
                if (maxTotal != null) {
                    cm.setMaxTotal(maxTotal);
                }

                Integer defaultMaxPerRoute = clientConfig.getDefaultMaxTotalConnectionPerRoute();
                if (defaultMaxPerRoute != null) {
                    cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
                }

                Map<HttpRoute, Integer> maxPerRoute = clientConfig.getMaxTotalConnectionPerRoute();
                for (HttpRoute route : maxPerRoute.keySet()) {
                    cm.setMaxPerRoute(route, maxPerRoute.get(route));
                }
                httpclient = new DefaultHttpClient(cm);
                log.debug("Multi Threaded http client created");
            } else {
                httpclient = new DefaultHttpClient();
                log.debug("Default http client is created without multi threaded option");
            }

            //set discovery
            if (clientConfig.isDiscoveryEnabled()) {
                log.info("Node Discovery Enabled...");
                NodeChecker nodeChecker = new NodeChecker(clientConfig, client);
                client.setNodeChecker(nodeChecker);
                nodeChecker.startAndWait();
            } else {
                log.info("Node Discovery Disabled...");
            }

            // set custom gson instance
            Gson gson = clientConfig.getGson();
            if (gson != null) {
                client.setGson(gson);
            }
        } else {
            log.debug("There is no configuration to create http client. Going to create simple client with default values");
            httpclient = new DefaultHttpClient();
            LinkedHashSet<String> servers = new LinkedHashSet<String>();
            servers.add("http://localhost:9200");
            client.setServers(servers);
        }
        client.setHttpClient(httpclient);
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

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
