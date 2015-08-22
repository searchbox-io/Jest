package io.searchbox.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class JestClientFactoryTest {

    

    @Test
    public void clientCreationWithDiscovery() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost:9200").discoveryEnabled(true).build());
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertTrue(jestClient != null);
        assertEquals(jestClient.getServerPoolSize(), 1);
    }

    @Test
    public void clientCreationWithNullClientConfig() {
        JestClientFactory factory = new JestClientFactory();
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertTrue(jestClient != null);
        assertEquals(jestClient.getServerPoolSize(), 1);
        assertEquals("server list should contain localhost:9200",
                "http://localhost:9200", jestClient.getNextServer());
    }

    
}
