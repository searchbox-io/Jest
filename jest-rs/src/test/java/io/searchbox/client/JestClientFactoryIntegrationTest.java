package io.searchbox.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.internal.InternalNode;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 0)
public class JestClientFactoryIntegrationTest extends ElasticsearchIntegrationTest {

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

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.settingsBuilder()
                .put(super.nodeSettings(nodeOrdinal))
                .put(RestController.HTTP_JSON_ENABLE, true)
                .put(InternalNode.HTTP_ENABLED, true)
                .build();
    }
}
