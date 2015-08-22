package io.searchbox.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.searchbox.rs.client.JestClientFactory;
import io.searchbox.rs.client.config.RsClientConfig;
import io.searchbox.rs.client.http.JestRsClient;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class JestClientFactoryTest {

    

    @Test
    public void clientCreationWithDiscovery() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new RsClientConfig.Builder("http://localhost:9200").discoveryEnabled(true).build());
        JestRsClient jestClient = (JestRsClient) factory.getObject();
        assertTrue(jestClient != null);
        assertEquals(jestClient.getServerPoolSize(), 1);
    }

    @Test
    public void clientCreationWithNullClientConfig() {
        JestClientFactory factory = new JestClientFactory();
        JestRsClient jestClient = (JestRsClient) factory.getObject();
        assertTrue(jestClient != null);
        assertEquals(jestClient.getServerPoolSize(), 1);
        assertEquals("server list should contain localhost:9200",
                "http://localhost:9200", jestClient.getNextServer());
    }

    
}
