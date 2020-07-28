package io.searchbox.client;

import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.http.JestHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;

import static java.util.Arrays.asList;
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
        assertEquals(150, defaultRequestConfig.getConnectTimeout());
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

    @Test
    public void clientCreationWithDiscoveryAndOverriddenNodeChecker() {
        JestClientFactory factory = Mockito.spy(new ExtendedJestClientFactory());
        HttpClientConfig httpClientConfig = Mockito.spy(new HttpClientConfig.Builder("http://somehost:9200")
                .discoveryEnabled(true)
                .build());
        factory.setHttpClientConfig(httpClientConfig);
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertTrue(jestClient != null);
        assertNotNull(jestClient.getAsyncClient());
        assertEquals(jestClient.getServerPoolSize(), 1);
        assertEquals("server list should contain localhost:9200",
                "http://somehost:9200", jestClient.getNextServer());
        Mockito.verify(factory, Mockito.times(1)).createNodeChecker(Mockito.any(JestHttpClient.class),
                                                                    Mockito.same(httpClientConfig));
    }

    @Test
    public void clientCreationWithPreemptiveAuth() {
        JestClientFactory factory = new JestClientFactory();
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("someUser", "somePassword"));
        HttpHost targetHost1 = new HttpHost("targetHostName1", 80, "http");
        HttpHost targetHost2 = new HttpHost("targetHostName2", 80, "http");

        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("someUri")
                .credentialsProvider(credentialsProvider)
                .preemptiveAuthTargetHosts(new HashSet<HttpHost>(asList(targetHost1, targetHost2)))
                .build();

        factory.setHttpClientConfig(httpClientConfig);
        JestHttpClient jestHttpClient = (JestHttpClient) factory.getObject();
        HttpClientContext httpClientContext = jestHttpClient.getHttpClientContextTemplate();

        assertNotNull(httpClientContext.getAuthCache().get(targetHost1));
        assertNotNull(httpClientContext.getAuthCache().get(targetHost2));
        assertEquals(credentialsProvider, httpClientContext.getCredentialsProvider());
    }

    class ExtendedJestClientFactory extends JestClientFactory {
        @Override
        protected NodeChecker createNodeChecker(JestHttpClient client, HttpClientConfig httpClientConfig) {
            return new OtherNodeChecker(client, httpClientConfig);
        }
    }

    class OtherNodeChecker extends NodeChecker {
        public OtherNodeChecker(JestClient jestClient, ClientConfig clientConfig) {
            super(jestClient, clientConfig);
        }
    }
}
