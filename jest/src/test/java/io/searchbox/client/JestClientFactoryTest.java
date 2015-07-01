package io.searchbox.client;

import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class JestClientFactoryTest {

    @Test
    public void clientCreationWithTimeout() {
        JestClientFactory factory = new JestClientFactory();
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder(
                "someUri").connTimeout(150).readTimeout(300).build();
        factory.setHttpClientConfig(httpClientConfig);
        final RequestConfig defaultRequestConfig = factory.getRequestConfig();
        assertNotNull(defaultRequestConfig);
        assertEquals(150, defaultRequestConfig.getConnectionRequestTimeout());
        assertEquals(300, defaultRequestConfig.getSocketTimeout());
    }

    @Test
    public void clientCreationWithDiscovery() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost:9200").discoveryEnabled(true).build());
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertTrue(jestClient != null);
        assertNotNull(jestClient.getAsyncClient());
        assertTrue(factory.getConnectionManager() instanceof BasicHttpClientConnectionManager);
        assertEquals(jestClient.getServerPoolSize(), 1);
    }

    @Test
    public void clientCreationWithNullClientConfig() {
        JestClientFactory factory = new JestClientFactory();
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertTrue(jestClient != null);
        assertNotNull(jestClient.getAsyncClient());
        assertEquals(jestClient.getServerPoolSize(), 1);
        assertEquals("server list should contain localhost:9200",
                "http://localhost:9200", jestClient.getNextServer());
    }

    @Test
    public void multiThreadedClientCreation() {
        JestClientFactory factory = new JestClientFactory();
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
        assertEquals(jestClient.getServerPoolSize(), 1);
        assertEquals("server list should contain localhost:9200", "http://localhost:9200", jestClient.getNextServer());

        final HttpClientConnectionManager connectionManager = factory.getConnectionManager();
        assertTrue(connectionManager instanceof PoolingHttpClientConnectionManager);
        assertEquals(10, ((PoolingHttpClientConnectionManager) connectionManager).getDefaultMaxPerRoute());
        assertEquals(20, ((PoolingHttpClientConnectionManager) connectionManager).getMaxTotal());
        assertEquals(5, ((PoolingHttpClientConnectionManager) connectionManager).getMaxPerRoute(routeOne));
        assertEquals(6, ((PoolingHttpClientConnectionManager) connectionManager).getMaxPerRoute(routeTwo));

        final NHttpClientConnectionManager nConnectionManager = factory.getAsyncConnectionManager();
        assertTrue(nConnectionManager instanceof PoolingNHttpClientConnectionManager);
        assertEquals(10, ((PoolingNHttpClientConnectionManager) nConnectionManager).getDefaultMaxPerRoute());
        assertEquals(20, ((PoolingNHttpClientConnectionManager) nConnectionManager).getMaxTotal());
        assertEquals(5, ((PoolingNHttpClientConnectionManager) nConnectionManager).getMaxPerRoute(routeOne));
        assertEquals(6, ((PoolingNHttpClientConnectionManager) nConnectionManager).getMaxPerRoute(routeTwo));
    }
}
