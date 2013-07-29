package io.searchbox.cluster;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.node.Node;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode(clusterName = "NHT", name = "Pony_NHT")
public class NodesHotThreadsIntegrationTest extends AbstractIntegrationTest {

    @ElasticsearchNode(clusterName = "NHT", name = "Annie_NHT")
    Node node2;

    @Test
    public void allNodesHotThreads() throws IOException {
        JestResult result = client.execute(new NodesHotThreads.Builder().build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        assertTrue(result.getJsonString().contains("out of 500ms"));
        assertTrue(result.getJsonString().contains("::: [Annie_NHT]["));
        assertTrue(result.getJsonString().contains("::: [Pony_NHT]["));
    }

    @Test
    public void singleNodeHotThreads() throws IOException {
        JestResult result = client.execute(new NodesHotThreads.Builder().addNode("Annie_NHT").build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        assertTrue(result.getJsonString().contains("::: [Annie_NHT]["));
        assertFalse(result.getJsonString().contains("::: [Pony_NHT]["));
    }

    @Test
    public void singleNodeHotThreadsWithCustomInterval() throws IOException {
        JestResult result = client.execute(new NodesHotThreads.Builder()
                .addNode("Annie_NHT")
                .interval("100ms")
                .build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        assertTrue(result.getJsonString().contains("out of 100ms"));
        assertTrue(result.getJsonString().contains("::: [Annie_NHT]["));
        assertFalse(result.getJsonString().contains("::: [Pony_NHT]["));
    }

}
