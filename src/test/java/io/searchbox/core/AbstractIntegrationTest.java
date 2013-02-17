package io.searchbox.core;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import io.searchbox.client.http.JestHttpClient;
import org.junit.Before;

import java.util.LinkedHashSet;

/**
 * @author Dogukan Sonmez
 */

public class AbstractIntegrationTest {

    protected JestClientFactory factory;

    protected JestHttpClient client;

    @Before
    public void setUp() throws Exception {
        factory = new JestClientFactory();
        ClientConfig clientConfig = new ClientConfig();
        LinkedHashSet<String> servers = new LinkedHashSet<String>();
        servers.add("http://localhost:9200");
        clientConfig.getProperties().put(ClientConstants.SERVER_LIST, servers);
        clientConfig.getProperties().put(ClientConstants.IS_MULTI_THREADED, true);

        factory.setClientConfig(clientConfig);

        client = (JestHttpClient) factory.getObject();
    }

}
