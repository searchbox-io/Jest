package io.searchbox.client;

import io.searchbox.client.http.ElasticSearchHttpClient;
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


public class ElasticSearchClientFactoryTest{

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
        ElasticSearchHttpClient elasticSearchClient = (ElasticSearchHttpClient) context.getBean(ElasticSearchClient.class);
        assertTrue(elasticSearchClient != null);
        assertTrue(elasticSearchClient.getHttpClient().getConnectionManager() instanceof PoolingClientConnectionManager);
        assertEquals(elasticSearchClient.getServers().size(),1);
        assertTrue(elasticSearchClient.getServers().contains("http://localhost:9200"));
    }

    @Test
    public void clientCreationWithNullClientConfig() {
        ElasticSearchHttpClient elasticSearchClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        assertTrue(elasticSearchClient != null);
        assertEquals(elasticSearchClient.getServers().size(),1);
        assertTrue(elasticSearchClient.getServers().contains("http://localhost:9200"));
    }


}
