package io.searchbox.cluster;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class NodesInfoIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void nodesInfoWithoutNodeAndInfo() throws IOException {
        JestResult result = client.execute(new NodesInfo.Builder().build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void nodesInfoWithNodeWithoutInfo() throws IOException {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("node1").build();
        JestResult result = client.execute(nodesInfo);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void nodesInfoWithoutNodeWithInfo() throws IOException {
        NodesInfo nodesInfo = new NodesInfo.Builder().os(true).build();
        JestResult result = client.execute(nodesInfo);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void nodesInfoWithNodeAndWithInfo() throws IOException {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("node1").os(true).build();
        JestResult result = client.execute(nodesInfo);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void nodesInfoWithMultipleNodeAndWithoutInfo() throws IOException {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("node1").addNode("node2").build();
        JestResult result = client.execute(nodesInfo);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void nodesInfoWithMultipleNodeAndMultipleInfo() throws IOException {
        NodesInfo nodesInfo = new NodesInfo.Builder()
                .addNode("node1")
                .addNode("node2")
                .process(true)
                .os(true)
                .build();
        JestResult result = client.execute(nodesInfo);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }
}
