package io.searchbox.client.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.Stats;
import org.apache.http.HttpHost;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 0)
public class JestHttpClientConfiguredProxyIntegrationTest extends ESIntegTestCase {

    private static final int PROXY_PORT = 8770;

    private AtomicInteger numProxyRequests = new AtomicInteger(0);
    private JestClientFactory factory = new JestClientFactory();
    private HttpProxyServer server = null;
    private JestHttpClient customJestClient = null;

    @Before
    public void setup() {
        server = DefaultHttpProxyServer
                .bootstrap()
                .withPort(PROXY_PORT)
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new HttpFiltersAdapter(originalRequest) {
                            @Override
                            public HttpResponse requestPre(HttpObject httpObject) {
                                if (httpObject instanceof HttpRequest) {
                                    numProxyRequests.incrementAndGet();
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
        if (customJestClient != null) customJestClient.shutdownClient();
    }

    @Test
    public void testConnectionThroughConfiguredProxy() throws IOException, ExecutionException, InterruptedException {
        internalCluster().ensureAtLeastNumDataNodes(1);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 1, cluster().httpAddresses().length);

        // sanity check - assert we cant connect without configuring proxy
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + cluster().httpAddresses()[0].getPort())
                .build());
        customJestClient = (JestHttpClient) factory.getObject();

        JestResult result = customJestClient.execute(new Stats.Builder().build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(0, numProxyRequests.intValue());
        customJestClient.shutdownClient();

        // test sync execution
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + cluster().httpAddresses()[0].getPort())
                .proxy(new HttpHost("localhost", PROXY_PORT))
                .build());
        customJestClient = (JestHttpClient) factory.getObject();

        result = customJestClient.execute(new Stats.Builder().build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1, numProxyRequests.intValue());
        customJestClient.shutdownClient();

        // test async execution
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + cluster().httpAddresses()[0].getPort())
                .proxy(new HttpHost("localhost", PROXY_PORT))
                .multiThreaded(true)
                .build());
        customJestClient = (JestHttpClient) factory.getObject();

        final CountDownLatch actionExecuted = new CountDownLatch(1);
        customJestClient.executeAsync(new Stats.Builder().build(), new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult result) {
                actionExecuted.countDown();
            }

            @Override
            public void failed(Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        boolean finishedAsync = actionExecuted.await(2, TimeUnit.SECONDS);
        if (!finishedAsync) {
            fail("Execution took too long to complete");
        }

        assertEquals(2, numProxyRequests.intValue());
        customJestClient.shutdownClient();
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return Settings.settingsBuilder()
                .put(super.nodeSettings(nodeOrdinal))
                .put(Node.HTTP_ENABLED, true)
                .build();
    }
}
