package io.searchbox.client;

import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import io.searchbox.client.http.JestHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

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
        assertTrue(jestClient.getHttpClient().getConnectionManager() instanceof PoolingClientConnectionManager);
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
        ClientConfig clientConfig = new ClientConfig();
        LinkedHashSet<String> servers = new LinkedHashSet<String>();
        servers.add("http://localhost:9200");
        clientConfig.getServerProperties().put(ClientConstants.SERVER_LIST, servers);
        clientConfig.getClientFeatures().put(ClientConstants.IS_MULTI_THREADED, true);
        clientConfig.getClientFeatures().put(ClientConstants.MAX_TOTAL_CONNECTION, 20);
        clientConfig.getClientFeatures().put(ClientConstants.DEFAULT_MAX_TOTAL_CONNECTION_PER_ROUTE, 10);

        HttpRoute routeOne = new HttpRoute(new HttpHost("http://test.localhost"));
        HttpRoute routeTwo = new HttpRoute(new HttpHost("http://localhost"));
        Map<HttpRoute, Integer> routeConnectionLimitMap = new HashMap<HttpRoute, Integer>();
        routeConnectionLimitMap.put(routeOne, 5);
        routeConnectionLimitMap.put(routeTwo, 6);
        clientConfig.getClientFeatures().put(ClientConstants.MAX_TOTAL_CONNECTION_PER_ROUTE, routeConnectionLimitMap);

        factory.setClientConfig(clientConfig);
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
