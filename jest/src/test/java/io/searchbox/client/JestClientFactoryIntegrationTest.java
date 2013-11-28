package io.searchbox.client;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchAdminClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.node.Node;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
public class JestClientFactoryIntegrationTest {

    @ElasticsearchNode
    Node first;

    @ElasticsearchNode(name = "2nd")
    Node node;
    @ElasticsearchAdminClient
    AdminClient adminClient;
    JestClientFactory factory = new JestClientFactory();

    @Test
    public void testDiscovery() throws InterruptedException {
        // wait for 2 active nodes
        adminClient.cluster().prepareHealth().setWaitForGreenStatus().
                setWaitForNodes("2").setWaitForRelocatingShards(0).execute().actionGet();

        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9200")
                .discoveryEnabled(true)
                .discoveryFrequency(1l, TimeUnit.SECONDS)
                .build());
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertNotNull(jestClient);

        // wait for NodeChecker to do the discovery
        Thread.sleep(3000);

        assertEquals("All 2 nodes should be discovered and be in the client's server list", 2, jestClient.getServers().size());

        // now close first client
        first.stop();

        // wait
        Thread.sleep(3000);

        assertEquals("Only 1 node should be in Jest's list", 1, jestClient.getServers().size());

    }
}
