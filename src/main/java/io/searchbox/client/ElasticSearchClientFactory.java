package io.searchbox.client;

import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import io.searchbox.client.http.ElasticSearchHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

import java.util.LinkedHashSet;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchClientFactory implements FactoryBean<ElasticSearchClient> {

    private static Logger log = Logger.getLogger(ElasticSearchClientFactory.class.getName());

    private ClientConfig clientConfig;

    public ElasticSearchClient getObject() {
        ElasticSearchHttpClient client = new ElasticSearchHttpClient();
        HttpClient httpclient;

        if (clientConfig != null) {
            log.debug("Creating HTTP client based on configuration");
            client.setServers((LinkedHashSet) clientConfig.getServerProperty(ClientConstants.SERVER_LIST));
            Boolean isMultiThreaded = (Boolean) clientConfig.getClientFuture(ClientConstants.IS_MULTI_THREADED);
            if (isMultiThreaded) {
                PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
                httpclient = new DefaultHttpClient(cm);
                log.debug("Multi Threaded http client created");
            } else {
                httpclient = new DefaultHttpClient();
                log.debug("Default http client is created without multi threaded option");
            }
        } else {
            log.debug("There is no configuration to create http client. Going to create simple client with default values");
            httpclient = new DefaultHttpClient();
            LinkedHashSet<String> servers = new LinkedHashSet<String>();
            servers.add("http://localhost:9200");
            client.setServers(servers);
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
