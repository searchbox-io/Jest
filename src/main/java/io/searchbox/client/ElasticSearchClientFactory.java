package io.searchbox.client;

import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import io.searchbox.client.http.ElasticSearchHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.springframework.beans.factory.FactoryBean;

import java.util.LinkedHashSet;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchClientFactory implements FactoryBean<ElasticSearchClient> {

    private ClientConfig clientConfig;

    public ElasticSearchClient getObject() {
        ElasticSearchHttpClient client = new ElasticSearchHttpClient();
        client.setServers((LinkedHashSet) clientConfig.getServerProperty(ClientConstants.SERVER_LIST));
        Boolean isMultiThreaded = (Boolean) clientConfig.getClientFuture(ClientConstants.IS_MULTI_THREADED);
        HttpClient httpclient;
        if (isMultiThreaded) {
            PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
            httpclient = new DefaultHttpClient(cm);
        } else {
            httpclient = new DefaultHttpClient();
        }
        client.setHttpClient(httpclient);
        return client;
    }

    public Class<?> getObjectType() {
        return ElasticSearchClient.class;
    }

    public boolean isSingleton() {
        return false;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
