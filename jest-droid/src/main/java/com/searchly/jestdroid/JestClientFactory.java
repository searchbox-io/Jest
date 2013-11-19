package com.searchly.jestdroid;

import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.PoolingClientConnectionManager;
import com.google.gson.Gson;
import io.searchbox.client.JestClient;
import io.searchbox.client.config.discovery.NodeChecker;
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
            HttpClient httpclient;
            client.setServers(droidClientConfig.getServerList());
            boolean isMultiThreaded = droidClientConfig.isMultiThreaded();
            if (isMultiThreaded) {
                PoolingClientConnectionManager cm = new PoolingClientConnectionManager();

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
                httpclient = new DefaultHttpClient(cm);
                log.debug("Multi Threaded http client created");
            } else {
                httpclient = new DefaultHttpClient();
                log.debug("Default http client is created without multi threaded option");
            }

            // set custom gson instance
            Gson gson = droidClientConfig.getGson();
            if (gson != null) {
                client.setGson(gson);
            }

            client.setHttpClient(httpclient);
            //set discovery (should be set after setting the httpClient on jestClient)
            if (droidClientConfig.isDiscoveryEnabled()) {
                log.info("Node Discovery Enabled...");
                NodeChecker nodeChecker = new NodeChecker(droidClientConfig, client);
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
