package io.searchbox.client;

import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import io.searchbox.client.http.JestHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.util.LinkedHashSet;

/**
 * @author Dogukan Sonmez
 */


public class JestClientFactory implements FactoryBean<JestClient> {

	final static Logger log = LoggerFactory.getLogger(JestClientFactory.class);

    private ClientConfig clientConfig;

    public JestClient getObject() {
        JestHttpClient client = new JestHttpClient();
        HttpClient httpclient;

        if (clientConfig != null) {
            log.debug("Creating HTTP client based on configuration");
            client.setServers((LinkedHashSet) clientConfig.getServerProperty(ClientConstants.SERVER_LIST));
            Boolean isMultiThreaded = (Boolean) clientConfig.getClientFeature(ClientConstants.IS_MULTI_THREADED);
            if (isMultiThreaded != null && isMultiThreaded) {
                PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
                httpclient = new DefaultHttpClient(cm);
                log.debug("Multi Threaded http client created");
            } else {
                httpclient = new DefaultHttpClient();
                log.debug("Default http client is created without multi threaded option");
            }
            if (clientConfig.getClientFeature(ClientConstants.DEFAULT_INDEX) != null) {
                client.registerDefaultIndex((String) clientConfig.getClientFeature(ClientConstants.DEFAULT_INDEX));
                if (clientConfig.getClientFeature(ClientConstants.DEFAULT_TYPE) != null) {
                    client.registerDefaultType((String) clientConfig.getClientFeature(ClientConstants.DEFAULT_TYPE));
                }
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
        return JestClient.class;
    }

    public boolean isSingleton() {
        return false;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
