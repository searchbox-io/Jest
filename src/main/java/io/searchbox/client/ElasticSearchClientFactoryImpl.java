package io.searchbox.client;

import io.searchbox.client.configuration.ClientConfig;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchClientFactoryImpl {

    private ClientConfig clientConfig;

    public ElasticSearchClient createHttpClient() {
        return null;
    }

    public ElasticSearchClient createThriftClient() {
        return null;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
