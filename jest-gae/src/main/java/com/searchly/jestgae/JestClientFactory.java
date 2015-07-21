package com.searchly.jestgae;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.gson.Gson;
import io.searchbox.client.JestClient;
import io.searchbox.client.config.discovery.NodeChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Scopewriter
 */
public class JestClientFactory {

    final static Logger log = LoggerFactory.getLogger(JestClientFactory.class);
    private GaeClientConfig gaeClientConfig;

    public JestClient getObject() {
        JestGaeClient client = new JestGaeClient();

        if (gaeClientConfig == null) {
            log.debug("There is no configuration to create http client. Going to create simple client with default values");
            gaeClientConfig = new GaeClientConfig.Builder("http://localhost:9200").build();
        }

        client.setRequestCompressionEnabled(gaeClientConfig.isRequestCompressionEnabled());
        client.setServers(gaeClientConfig.getServerList());

        // Set Fetch options which contains the request timeout, follow redirects etc.
        FetchOptions fetchOptions = gaeClientConfig.getFetchOptions();
        if (fetchOptions == null) {
            log.info("Using default FetchOptions instance");
            client.setFetchOptions(FetchOptions.Builder.withDefaults());
        } else {
            log.info("Using custom FetchOptions instance");
            client.setFetchOptions(fetchOptions);
        }

        // set custom gson instance
        Gson gson = gaeClientConfig.getGson();
        if (gson == null) {
            log.info("Using default GSON instance");
        } else {
            log.info("Using custom GSON instance");
            client.setGson(gson);
        }

        // set discovery (should be set after setting the httpClient on jestClient)
        if (gaeClientConfig.isDiscoveryEnabled()) {
            log.info("Node Discovery enabled...");
            NodeChecker nodeChecker = new NodeChecker(client, gaeClientConfig);
            client.setNodeChecker(nodeChecker);
            nodeChecker.startAsync();
            nodeChecker.awaitRunning();
        } else {
            log.info("Node Discovery disabled...");
        }

        return client;
    }

    public void setGaeClientConfig(GaeClientConfig gaeClientConfig) {
        this.gaeClientConfig = gaeClientConfig;
    }
}
