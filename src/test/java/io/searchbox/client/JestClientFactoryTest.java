package io.searchbox.client;

import io.searchbox.client.http.JestHttpClient;
import io.searchbox.configuration.SpringClientTestConfiguration;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */


public class JestClientFactoryTest {

    private AnnotationConfigApplicationContext context;

    @Before
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(SpringClientTestConfiguration.class);
    }

    @After
    public void tearDown() throws Exception {
        context.close();
    }

    @Test
    public void clientCreation() {
        JestHttpClient jestClient = (JestHttpClient) context.getBean(JestClient.class);
        assertTrue(jestClient != null);
        assertTrue(jestClient.getHttpClient().getConnectionManager() instanceof PoolingClientConnectionManager);
        assertEquals(jestClient.getServers().size(), 1);
        assertTrue(jestClient.getServers().contains("http://localhost:9200"));
    }

    @Test
    public void clientCreationWithNullClientConfig() {
        JestHttpClient jestClient = (JestHttpClient) new JestClientFactory().getObject();
        assertTrue(jestClient != null);
        assertEquals(jestClient.getServers().size(), 1);
        assertTrue(jestClient.getServers().contains("http://localhost:9200"));
    }


}
