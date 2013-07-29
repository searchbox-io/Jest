package io.searchbox.common;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.http.JestHttpClient;
import org.junit.Before;

/**
 * @author Dogukan Sonmez
 */

public abstract class AbstractIntegrationTest {

    protected JestClientFactory factory;
    protected JestHttpClient client;

    protected String getPort(){
        return "9200";
    }

    @Before
    public void setUp() throws Exception {
        factory = new JestClientFactory();
        ClientConfig clientConfig = new ClientConfig.Builder("http://localhost:" + getPort()).multiThreaded(true).build();

        factory.setClientConfig(clientConfig);

        client = (JestHttpClient) factory.getObject();
    }

}
