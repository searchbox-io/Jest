package io.searchbox.client;

import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.google.gson.Gson;

import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.http.JestHttpClient;

/**
 * @author Dogukan Sonmez
 */
public class JestClientFactory {

    final static Logger log = Logger.getLogger(JestClientFactory.class.getName());
    private HttpClientConfig httpClientConfig;

    public JestClient getObject() {
        JestHttpClient client = new JestHttpClient();

        if (httpClientConfig == null) {
            log.finest("There is no configuration to create http client. Going to create simple client with default values");
            httpClientConfig = new HttpClientConfig.Builder("http://localhost:9200").build();
        }

        client.setRequestCompressionEnabled(httpClientConfig.isRequestCompressionEnabled());
        client.setServers(httpClientConfig.getServerList());
        client.setHttpClient(createHttpClient());

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
            NodeChecker nodeChecker = new NodeChecker(client, httpClientConfig);
            client.setNodeChecker(nodeChecker);
            nodeChecker.startAsync();
            nodeChecker.awaitRunning();
        } else {
            log.info("Node Discovery disabled...");
        }

        return client;
    }

    public void setHttpClientConfig(HttpClientConfig httpClientConfig) {
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
