package io.searchbox.cluster;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class NodesInfoIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void nodesInfoWithoutNodeAndInfo() {
        try {
            JestResult result = client.execute(new NodesInfo.Builder().build());
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void nodesInfoWithNodeWithoutInfo() {
        try {
            NodesInfo nodesInfo = new NodesInfo.Builder().addNode("node1").build();
            JestResult result = client.execute(nodesInfo);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void nodesInfoWithoutNodeWithInfo() {
        try {
            NodesInfo nodesInfo = new NodesInfo.Builder().os(true).build();
            JestResult result = client.execute(nodesInfo);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void nodesInfoWithNodeAndWithInfo() {
        try {
            NodesInfo nodesInfo = new NodesInfo.Builder().addNode("node1").os(true).build();
            JestResult result = client.execute(nodesInfo);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void nodesInfoWithMultipleNodeAndWithoutInfo() {
        try {
            NodesInfo nodesInfo = new NodesInfo.Builder().addNode("node1").addNode("node2").build();
            JestResult result = client.execute(nodesInfo);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void nodesInfoWithMultipleNodeAndMultipleInfo() {
        try {
            NodesInfo nodesInfo = new NodesInfo.Builder()
                    .addNode("node1")
                    .addNode("node2")
                    .process(true)
                    .os(true)
                    .build();
            JestResult result = client.execute(nodesInfo);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }
}
