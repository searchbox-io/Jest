package io.searchbox.cluster;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 2)
public class NodesHotThreadsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void allNodesHotThreads() throws IOException {
        JestResult result = client.execute(new NodesHotThreads.Builder().build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        assertTrue(result.getJsonString().contains("out of 500ms"));
        assertTrue("Result should contain info for first node", result.getJsonString().contains("::: [node_0]["));
        assertTrue("Result should contain info for second node", result.getJsonString().contains("::: [node_1]["));
    }

    @Test
    public void singleNodeHotThreads() throws IOException {
        JestResult result = client.execute(new NodesHotThreads.Builder().addNode("node_0").build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        assertTrue(result.getJsonString().contains("::: [node_0]["));
        assertFalse(result.getJsonString().contains("::: [node_1]["));
    }

    @Test
    public void singleNodeHotThreadsWithCustomInterval() throws IOException {
        JestResult result = client.execute(new NodesHotThreads.Builder()
                .addNode("node_0")
                .interval("100ms")
                .build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        assertTrue(result.getJsonString().contains("out of 100ms"));
        assertTrue(result.getJsonString().contains("::: [node_0]["));
        assertFalse(result.getJsonString().contains("::: [node_1]["));
    }

}
