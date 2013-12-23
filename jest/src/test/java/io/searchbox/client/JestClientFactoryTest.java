package io.searchbox.client;

import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class JestClientFactoryTest {

    private JestClientFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new JestClientFactory();
    }

    @Test
    public void clientCreation() {
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertTrue(jestClient != null);
        assertNotNull(jestClient.getAsyncClient());
        assertTrue(jestClient.getHttpClient().getConnectionManager() instanceof BasicClientConnectionManager);
        assertEquals(jestClient.getServers().size(), 1);
        assertTrue(jestClient.getServers().contains("http://localhost:9200"));
    }

    @Test
    public void clientCreationWithTimeout() {
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder(
                "someUri").connTimeout(150).readTimeout(300).build();
        factory.setHttpClientConfig(httpClientConfig);
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();

        assertEquals(150, jestClient.getHttpClient().getParams().getParameter(CoreConnectionPNames.CONNECTION_TIMEOUT));
        assertEquals(300, jestClient.getHttpClient().getParams().getParameter(CoreConnectionPNames.SO_TIMEOUT));
    }

    @Test
    public void clientCreationWithDiscovery() {
        factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost:9200").discoveryEnabled(true).build());
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertTrue(jestClient != null);
        assertNotNull(jestClient.getAsyncClient());
        assertTrue(jestClient.getHttpClient().getConnectionManager() instanceof BasicClientConnectionManager);
        assertEquals(jestClient.getServers().size(), 1);
        assertTrue(jestClient.getServers().contains("http://localhost:9200"));
    }

    @Test
    public void clientCreationWithNullClientConfig() {
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertTrue(jestClient != null);
        assertNotNull(jestClient.getAsyncClient());
        assertEquals(jestClient.getServers().size(), 1);
        assertTrue(jestClient.getServers().contains("http://localhost:9200"));
    }

    @Test
    public void multiThreadedClientCreation() {
        HttpRoute routeOne = new HttpRoute(new HttpHost("http://test.localhost"));
        HttpRoute routeTwo = new HttpRoute(new HttpHost("http://localhost"));

        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("http://localhost:9200")
                .multiThreaded(true)
                .maxTotalConnection(20)
                .defaultMaxTotalConnectionPerRoute(10)
                .maxTotalConnectionPerRoute(routeOne, 5)
                .maxTotalConnectionPerRoute(routeTwo, 6)
                .build();

        factory.setHttpClientConfig(httpClientConfig);
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();

        assertTrue(jestClient != null);
        assertNotNull(jestClient.getAsyncClient());
        assertTrue(jestClient.getHttpClient().getConnectionManager() instanceof PoolingClientConnectionManager);
        assertEquals(10, ((PoolingClientConnectionManager) jestClient.getHttpClient().getConnectionManager()).getDefaultMaxPerRoute());
        assertEquals(20, ((PoolingClientConnectionManager) jestClient.getHttpClient().getConnectionManager()).getMaxTotal());
        assertEquals(5, ((PoolingClientConnectionManager) jestClient.getHttpClient().getConnectionManager()).getMaxPerRoute(routeOne));
        assertEquals(6, ((PoolingClientConnectionManager) jestClient.getHttpClient().getConnectionManager()).getMaxPerRoute(routeTwo));

        assertEquals(jestClient.getServers().size(), 1);
        assertTrue(jestClient.getServers().contains("http://localhost:9200"));
    }


}
