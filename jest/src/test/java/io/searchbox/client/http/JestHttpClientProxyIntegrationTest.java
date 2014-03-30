package io.searchbox.client.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.Status;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.*;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numNodes = 0)
public class JestHttpClientProxyIntegrationTest extends ElasticsearchIntegrationTest {

    private AtomicInteger numProxyRequests = new AtomicInteger(0);
    private JestClientFactory factory = new JestClientFactory();
    private HttpProxyServer server = null;

    private static String nonProxyHostsDefault;
    private static String proxyHostDefault;
    private static String proxyPortDefault;
    private static String useSystemProxiesDefault;

    @BeforeClass
    public static void setupOnce() throws URISyntaxException {
        proxyHostDefault = System.getProperty("http.proxyHost");
        proxyPortDefault = System.getProperty("http.proxyPort");
        useSystemProxiesDefault = System.getProperty("java.net.useSystemProxies");

        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "8790");
        nonProxyHostsDefault = System.getProperty("http.nonProxyHosts");
        System.setProperty("http.nonProxyHosts", ""); // we want localhost to go through proxy
        System.setProperty("java.net.useSystemProxies", "true");
    }

    @Before
    public void setup() {
        server = DefaultHttpProxyServer
                .bootstrap()
                .withPort(8790)
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new HttpFiltersAdapter(originalRequest) {
                            @Override
                            public HttpResponse requestPre(HttpObject httpObject) {
                                if (httpObject instanceof HttpRequest) {
                                    if (((HttpRequest) httpObject).getUri().contains("localhost:9200")) {
                                        numProxyRequests.incrementAndGet();
                                    }
                                }
                                return null;
                            }

                            @Override
                            public HttpResponse requestPost(HttpObject httpObject) {
                                return null;
                            }

                            @Override
                            public HttpObject responsePre(HttpObject httpObject) {
                                return httpObject;
                            }

                            @Override
                            public HttpObject responsePost(HttpObject httpObject) {
                                return httpObject;
                            }
                        };
                    }
                })
                .start();
    }

    @After
    public void destroy() {
        if (server != null) server.stop();
    }

    @AfterClass
    public static void destroyOnce() {
        rollbackSystemProperty("http.proxyHost", proxyHostDefault);
        rollbackSystemProperty("http.proxyPort", proxyPortDefault);
        rollbackSystemProperty("http.nonProxyHosts", nonProxyHostsDefault);
        rollbackSystemProperty("java.net.useSystemProxies", useSystemProxiesDefault);
    }

    private static void rollbackSystemProperty(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }

    @Test
    public void testConnectionThroughDefaultProxy() throws IOException, ExecutionException, InterruptedException {
        cluster().ensureAtLeastNumNodes(1);

        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9200")
                .build());
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertNotNull(jestClient);

        JestResult result = jestClient.execute(new Status.Builder().build());
        assertNotNull(result);
        assertEquals(1, numProxyRequests.intValue());

        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9200")
                .multiThreaded(true)
                .build());
        jestClient = (JestHttpClient) factory.getObject();
        assertNotNull(jestClient);

        final AtomicBoolean actionExecuted = new AtomicBoolean(false);
        jestClient.executeAsync(new Status.Builder().build(), new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult result) {
                actionExecuted.set(true);
            }

            @Override
            public void failed(Exception ex) {
                actionExecuted.set(false);
            }
        });
        int retries = 0;
        while (!actionExecuted.get() && retries < 10) {
            Thread.sleep(200);
            retries++;
        }
        assertEquals(2, numProxyRequests.intValue());
    }
}
