package io.searchbox.common;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
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
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("http://localhost:" + getPort()).multiThreaded(true).build();

        factory.setHttpClientConfig(httpClientConfig);

        client = (JestHttpClient) factory.getObject();
    }

}
