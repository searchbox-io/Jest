package io.searchbox.rs.client;

import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.google.gson.Gson;

import io.searchbox.client.JestClient;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.rs.client.config.RsClientConfig;
import io.searchbox.rs.client.http.JestRsClient;

/**
 * Used to build Jest clients. This one builds the JAX-RS version.
 * 
 * @author Dogukan Sonmez
 */
public class JestClientFactory {

    final static Logger log = Logger.getLogger(JestClientFactory.class.getName());
    private RsClientConfig httpClientConfig;

    public JestClient getObject() {
        JestRsClient client = new JestRsClient();

        if (httpClientConfig == null) {
            log.finest("There is no configuration to create http client. Going to create simple client with default values");
            httpClientConfig = new RsClientConfig.Builder("http://localhost:9200").build();
        }

        client.setRequestCompressionEnabled(httpClientConfig.isRequestCompressionEnabled());
        client.setServers(httpClientConfig.getServerList());
        client.setHttpClient(createHttpClient());

        // set custom gson instance
        Gson gson = httpClientConfig.getGson();
        if (gson == null) {
            log.finest("Using default GSON instance");
        } else {
            log.finest("Using custom GSON instance");
            client.setGson(gson);
        }

        // set discovery (should be set after setting the httpClient on jestClient)
        if (httpClientConfig.isDiscoveryEnabled()) {
            log.finest("Node Discovery enabled...");
            NodeChecker nodeChecker = new NodeChecker(client, httpClientConfig);
            client.setNodeChecker(nodeChecker);
            nodeChecker.startAsync();
            nodeChecker.awaitRunning();
        } else {
            log.finest("Node Discovery disabled...");
        }

        return client;
    }

    public void setHttpClientConfig(RsClientConfig httpClientConfig) {
        this.httpClientConfig = httpClientConfig;
    }

    /**
     * Creates the JAX-RS client. This is an extension point that can be used to
     * modify how the client is built.
     * 
     * @return JAX-RS client.
     */
    protected Client createHttpClient() {
        return ClientBuilder.newClient();
    }

}
