package io.searchbox.client;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchAdminClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.config.ClientConfig;
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
@ElasticsearchNode
public class JestClientFactoryIntegrationTest {


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

        factory.setClientConfig(new ClientConfig
                .Builder("http://localhost:9200")
                .discoveryEnabled(true)
                .discoveryFrequency(1l, TimeUnit.SECONDS)
                .build());
        JestHttpClient jestClient = (JestHttpClient) factory.getObject();
        assertNotNull(jestClient);

        // wait for NodeChecker to do the discovery
        Thread.sleep(3000);

        assertEquals("All 2 nodes should be discovered and be in the client's server list", 2, jestClient.getServers().size());
    }
}
