package com.searchly.jestdroid;

import com.google.gson.Gson;
import io.searchbox.client.JestClient;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.config.idle.IdleConnectionReaper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author cihat.keser
 */
public class JestClientFactory {
    final static Logger log = LoggerFactory.getLogger(JestClientFactory.class);
    private DroidClientConfig droidClientConfig;

    public JestClient getObject() {
        JestDroidClient client = new JestDroidClient();

        if (droidClientConfig != null) {
            log.debug("Creating HTTP client based on configuration");
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            client.setServers(droidClientConfig.getServerList());
            boolean isMultiThreaded = droidClientConfig.isMultiThreaded();
            if (isMultiThreaded) {
                PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

                Integer maxTotal = droidClientConfig.getMaxTotalConnection();
                if (maxTotal != null) {
                    cm.setMaxTotal(maxTotal);
                }

                Integer defaultMaxPerRoute = droidClientConfig.getDefaultMaxTotalConnectionPerRoute();
                if (defaultMaxPerRoute != null) {
                    cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
                }

                Map<HttpRoute, Integer> maxPerRoute = droidClientConfig.getMaxTotalConnectionPerRoute();
                for (HttpRoute route : maxPerRoute.keySet()) {
                    cm.setMaxPerRoute(route, maxPerRoute.get(route));
                }
                httpClientBuilder.setConnectionManager(cm);
                log.debug("Multi Threaded http client created");

                // schedule idle connection reaping if configured
                if (droidClientConfig.getMaxConnectionIdleTime() > 0) {
                    log.info("Idle connection reaping enabled...");

                    IdleConnectionReaper reaper = new IdleConnectionReaper(droidClientConfig, new DroidReapableConnectionManager(cm));
                    client.setIdleConnectionReaper(reaper);
                    reaper.startAsync();
                    reaper.awaitRunning();
                }

            } else {
                log.debug("Default http client is created without multi threaded option");
            }

            httpClientBuilder.setDefaultRequestConfig(
                    RequestConfig.custom()
                            .setConnectTimeout(droidClientConfig.getConnTimeout())
                            .setSocketTimeout(droidClientConfig.getReadTimeout())
                            .build()
            );

            // set custom gson instance
            Gson gson = droidClientConfig.getGson();
            if (gson != null) {
                client.setGson(gson);
            }

            client.setHttpClient(httpClientBuilder.build());
            //set discovery (should be set after setting the httpClient on jestClient)
            if (droidClientConfig.isDiscoveryEnabled()) {
                log.info("Node Discovery Enabled...");
                NodeChecker nodeChecker = new NodeChecker(droidClientConfig, client);
                client.setNodeChecker(nodeChecker);
                nodeChecker.startAsync();
                nodeChecker.awaitRunning();
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

        return client;
    }

    public Class<?> getObjectType() {
        return JestClient.class;
    }

    public boolean isSingleton() {
        return false;
    }

    public void setDroidClientConfig(DroidClientConfig droidClientConfig) {
        this.droidClientConfig = droidClientConfig;
    }
}
