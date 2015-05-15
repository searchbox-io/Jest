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
import org.apache.http.HttpHost;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.internal.InternalNode;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 0)
public class JestHttpClientConfiguredProxyIntegrationTest extends ElasticsearchIntegrationTest {

    private static final int PROXY_PORT = 8770;

    private AtomicInteger numProxyRequests = new AtomicInteger(0);
    private JestClientFactory factory = new JestClientFactory();
    private HttpProxyServer server = null;

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
    }

    @Test
    public void testConnectionThroughConfiguredProxy() throws IOException, ExecutionException, InterruptedException {
        internalCluster().ensureAtLeastNumDataNodes(1);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 1, cluster().httpAddresses().length);

        // sanity check - assert we cant connect without configuring proxy
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + cluster().httpAddresses()[0].getPort())
                .build());
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();

        JestResult result = jestClient.execute(new Status.Builder().build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(0, numProxyRequests.intValue());
        jestClient.shutdownClient();

        // test sync execution
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + cluster().httpAddresses()[0].getPort())
                .proxy(new HttpHost("localhost", PROXY_PORT))
                .build());
        jestClient = (JestHttpClient) factory.getObject();

        result = jestClient.execute(new Status.Builder().build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1, numProxyRequests.intValue());
        jestClient.shutdownClient();

        // test async execution
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + cluster().httpAddresses()[0].getPort())
                .proxy(new HttpHost("localhost", PROXY_PORT))
                .multiThreaded(true)
                .build());
        jestClient = (JestHttpClient) factory.getObject();

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
        jestClient.shutdownClient();
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.settingsBuilder()
                .put(super.nodeSettings(nodeOrdinal))
                .put(RestController.HTTP_JSON_ENABLE, true)
                .put(InternalNode.HTTP_ENABLED, true)
                .build();
    }
}
