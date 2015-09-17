package io.searchbox.client;

import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.cluster.Health;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESIntegTestCase.ClusterScope;
import org.elasticsearch.test.ESIntegTestCase.Scope;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * @author cihat keser
 */
@ClusterScope(scope = Scope.TEST, numDataNodes = 0)
public class JestClientFactoryIntegrationTest extends ESIntegTestCase {

    JestClientFactory factory = new JestClientFactory();

    @Test
    public void testDiscovery() throws InterruptedException, IOException {
        // wait for 4 active nodes
        internalCluster().ensureAtLeastNumDataNodes(4);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 4, cluster().httpAddresses().length);

        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + cluster().httpAddresses()[0].getPort())
                .discoveryEnabled(true)
                .discoveryFrequency(500l, TimeUnit.MILLISECONDS)
                .build());
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertNotNull(jestClient);

        // wait for NodeChecker to do the discovery
        Thread.sleep(3000);

        assertEquals(
                "All 4 nodes should be discovered and be in the client's server list",
                4,
                jestClient.getServerPoolSize()
        );

        internalCluster().ensureAtMostNumDataNodes(3);

        int numServers = 0;
        int retries = 0;
        while (numServers != 3 && retries < 30) {
            numServers = jestClient.getServerPoolSize();
            retries++;
            Thread.sleep(1000);
        }

        assertEquals(
                "Only 3 nodes should be in Jest's list",
                3,
                jestClient.getServerPoolSize()
        );
        jestClient.shutdownClient();
    }

    @Test
    public void testIdleConnectionReaper() throws Exception {
        internalCluster().ensureAtLeastNumDataNodes(3);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 3, cluster().httpAddresses().length);

        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + cluster().httpAddresses()[0].getPort())
                .multiThreaded(true)
                .discoveryEnabled(true)
                .discoveryFrequency(100l, TimeUnit.MILLISECONDS)
                .maxConnectionIdleTime(1500L, TimeUnit.MILLISECONDS)
                .maxTotalConnection(75)
                .defaultMaxTotalConnectionPerRoute(75)
                .build());
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertNotNull(jestClient);

        Thread.sleep(300L); // Allow nodechecker to do it's thing and use at least one connection in the pool

        // Ask for the cluster health just to use some connections
        int maxPoolSize = getPoolSize(jestClient);
        for (int x = 0; x < 5; ++x) {
            jestClient.execute(new Health.Builder().build());
            maxPoolSize = Math.max(maxPoolSize, getPoolSize(jestClient));
        }

        Thread.sleep(3200); // Allow cxn reaper a chance to do it's thing

        int newPoolSize = getPoolSize(jestClient);

        // The new pool size should be much less than the maxPoolSize since the idle connection reaper will have run
        // twice in the time between maxPoolSize's last calculation and now.  There should really only be 1-2 connections
        // in the pool at this point since our idle timeout is set so low for this test.
        assertTrue(maxPoolSize > newPoolSize);
        jestClient.shutdownClient();
    }

    @Test
    public void testNoIdleConnectionReaper() throws Exception {
        internalCluster().ensureAtLeastNumDataNodes(3);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 3, cluster().httpAddresses().length);

        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:" + cluster().httpAddresses()[0].getPort())
                .multiThreaded(true)
                .discoveryEnabled(true)
                .discoveryFrequency(100l, TimeUnit.MILLISECONDS)
                .maxTotalConnection(75)
                .defaultMaxTotalConnectionPerRoute(75)
                .build());
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertNotNull(jestClient);

        Thread.sleep(300L); // Allow nodechecker to do it's thing and use at least one connection in the pool

        // Ask for the cluster health just to use some connections and create a little white noise
        int maxPoolSize = getPoolSize(jestClient);
        for (int x = 0; x < 5; ++x) {
            jestClient.execute(new Health.Builder().build());
            maxPoolSize = Math.max(maxPoolSize, getPoolSize(jestClient));
        }

        Thread.sleep(3000); // Allow for a quiesce period of no activity (except for nodechecker)

        int newPoolSize = getPoolSize(jestClient);

        // These two values being equal proves that connections returned to the pool stick around for some non-zero
        // duration of time while they wait to be re-leased.  It's impractical to prove in an integration test that they
        // can in fact stay around for over an hour without ever being used (by which time the server has most certainly
        // closed the connection).
        assertEquals(maxPoolSize, newPoolSize);
        jestClient.shutdownClient();
    }

    /**
     * Forgive me these sins.  This is the only way I can think of to determine the *actual* size of the connection pool
     * without wrapping large quantities of the underlying client.
     * <p>
     * This whole method is cheating and full of bad examples.  Don't copy this.  You've been warned.
     */
    private int getPoolSize(JestHttpClient client) throws Exception {
        try {
            Field fieldHttpClient = client.getClass().getDeclaredField("httpClient");
            fieldHttpClient.setAccessible(true);
            Object objInternalHttpClient = fieldHttpClient.get(client);

            Field fieldConnectionManager = objInternalHttpClient.getClass().getDeclaredField("connManager");
            fieldConnectionManager.setAccessible(true);
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = (PoolingHttpClientConnectionManager) fieldConnectionManager.get(objInternalHttpClient);

            PoolStats poolStats = poolingHttpClientConnectionManager.getTotalStats();

            return poolStats.getAvailable() + poolStats.getLeased();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return Settings.settingsBuilder()
                .put(super.nodeSettings(nodeOrdinal))
                .put(Node.HTTP_ENABLED, true)
                .build();
    }
}
