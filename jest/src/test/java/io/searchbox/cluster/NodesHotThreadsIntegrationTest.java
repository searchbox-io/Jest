package io.searchbox.cluster;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.GLOBAL, numDataNodes = 2)
public class NodesHotThreadsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void allNodesHotThreads() throws IOException {
        String firstNode = internalCluster().getNodeNames()[0];
        String secondNode = internalCluster().getNodeNames()[1];

        JestResult result = client.execute(new NodesHotThreads.Builder().build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        assertTrue(result.getJsonString().contains("out of 500ms"));
        assertTrue("Result should contain info for first node", result.getJsonString().contains("::: [" + firstNode + "]["));
        assertTrue("Result should contain info for second node", result.getJsonString().contains("::: [" + secondNode + "]["));
    }

    @Test
    public void singleNodeHotThreads() throws IOException {
        String firstNode = internalCluster().getNodeNames()[0];
        String secondNode = internalCluster().getNodeNames()[1];

        JestResult result = client.execute(new NodesHotThreads.Builder().addNode(firstNode).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        assertTrue(result.getJsonString().contains("::: [" + firstNode + "]["));
        assertFalse(result.getJsonString().contains("::: [" + secondNode + "]["));
    }

    @Test
    public void singleNodeHotThreadsWithCustomInterval() throws IOException {
        String firstNode = internalCluster().getNodeNames()[0];
        String secondNode = internalCluster().getNodeNames()[1];

        JestResult result = client.execute(new NodesHotThreads.Builder()
                .addNode(firstNode)
                .interval("100ms")
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        assertTrue(result.getJsonString().contains("out of 100ms"));
        assertTrue(result.getJsonString().contains("::: [" + firstNode + "]["));
        assertFalse(result.getJsonString().contains("::: [" + secondNode + "]["));
    }

}
