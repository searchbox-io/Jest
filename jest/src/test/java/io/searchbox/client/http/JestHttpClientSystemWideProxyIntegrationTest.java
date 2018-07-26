package io.searchbox.client.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.Stats;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.transport.Netty4Plugin;
import org.junit.*;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 0)
public class JestHttpClientSystemWideProxyIntegrationTest extends ESIntegTestCase {


    private static final int PROXY_PORT = 8790;

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
        System.setProperty("http.proxyPort", Integer.toString(PROXY_PORT));
        nonProxyHostsDefault = System.getProperty("http.nonProxyHosts");
        System.setProperty("http.nonProxyHosts", ""); // we want localhost to go through proxy
        System.setProperty("java.net.useSystemProxies", "true");
    }

    @Before
    public void setup() {
        server = DefaultHttpProxyServer
                .bootstrap()
                .withPort(PROXY_PORT)
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new HttpFiltersAdapter(originalRequest) {
                            @Override
                            public void proxyToServerRequestSent() {
                                numProxyRequests.incrementAndGet();
                            }
                        };
                    }
                }).start();
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
        internalCluster().ensureAtLeastNumDataNodes(1);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 1, cluster().httpAddresses().length);

        // test sync execution
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + cluster().httpAddresses()[0].getPort())
                .build());
        try (JestHttpClient jestClient = (JestHttpClient) factory.getObject()) {
            assertNotNull(jestClient);

            JestResult result = jestClient.execute(new Stats.Builder().build());
            assertTrue(result.getErrorMessage(), result.isSucceeded());
            assertEquals(1, numProxyRequests.intValue());
        }

        // test async execution
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + cluster().httpAddresses()[0].getPort())
                .multiThreaded(true)
                .build());
        try (JestHttpClient jestClient = (JestHttpClient) factory.getObject()) {
            assertNotNull(jestClient);

            final CountDownLatch actionExecuted = new CountDownLatch(1);
            jestClient.executeAsync(new Stats.Builder().build(), new JestResultHandler<JestResult>() {
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
        }
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return Settings.builder().put(super.nodeSettings(nodeOrdinal))
                .put(NetworkModule.HTTP_TYPE_KEY, Netty4Plugin.NETTY_HTTP_TRANSPORT_NAME)
                .put(NetworkModule.HTTP_ENABLED.getKey(), true)
                .build();
    }

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return Collections.singletonList(Netty4Plugin.class);
    }
}
