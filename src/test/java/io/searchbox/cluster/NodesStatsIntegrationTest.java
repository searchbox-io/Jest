package io.searchbox.cluster;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import com.google.gson.JsonObject;
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
@ElasticsearchNode(name = "Pony")
public class NodesStatsIntegrationTest extends AbstractIntegrationTest {

    @ElasticsearchNode(name = "Annie")
    Node node2;

    @Test
    public void nodesStatsAllWithClear() throws IOException {
        JestResult result = client.execute(new NodesStats.Builder()
                .clear(true)
                .build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        assertEquals("2 nodes expected in stats response", 2, nodes.entrySet().size());
        JsonObject node = nodes.getAsJsonObject("1");
        assertNotNull(node);

        // check for the default node stats
        assertNotNull(node.get("timestamp"));
        assertNotNull(node.get("name"));
        assertNotNull(node.get("transport_address"));
        assertNotNull(node.get("hostname"));
        assertNotNull(node.get("attributes"));

        // node stats should only contain the default stats as we set clear=true
        assertEquals(5, node.entrySet().size());
    }

    @Test
    public void nodesStatsWithClear() throws IOException {
        JestResult result = client.execute(new NodesStats.Builder()
                .addNode("Pony")
                .clear(true)
                .build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        assertEquals("only 1 node expected in stats response", 1, nodes.entrySet().size());
        JsonObject node = nodes.getAsJsonObject("1");
        assertNotNull(node);

        // check for the default node stats
        assertNotNull(node.get("timestamp"));
        assertNotNull(node.get("name"));
        assertNotNull(node.get("transport_address"));
        assertNotNull(node.get("hostname"));
        assertNotNull(node.get("attributes"));

        // node stats should only contain the default stats as we set clear=true
        assertEquals(5, node.entrySet().size());
    }

    @Test
    public void nodesStatsWithClearAndIndices() throws IOException {
        JestResult result = client.execute(new NodesStats.Builder()
                .addNode("Pony")
                .clear(true)
                .indices(true)
                .build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        assertEquals("only 1 node expected in stats response", 1, nodes.entrySet().size());
        JsonObject node = nodes.getAsJsonObject("1");
        assertNotNull(node);

        // check for the default node stats
        assertNotNull(node.get("timestamp"));
        assertNotNull(node.get("name"));
        assertNotNull(node.get("transport_address"));
        assertNotNull(node.get("hostname"));
        assertNotNull(node.get("attributes"));

        assertNotNull(node.get("indices"));

        assertEquals(6, node.entrySet().size());
    }

    @Test
    public void nodesStatsWithClearAndIndicesAndJvm() throws IOException {
        JestResult result = client.execute(new NodesStats.Builder()
                .addNode("Pony")
                .clear(true)
                .indices(true)
                .jvm(true)
                .build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        assertEquals("only 1 node expected in stats response", 1, nodes.entrySet().size());
        JsonObject node = nodes.getAsJsonObject("1");
        assertNotNull(node);

        // check for the default node stats
        assertNotNull(node.get("timestamp"));
        assertNotNull(node.get("name"));
        assertNotNull(node.get("transport_address"));
        assertNotNull(node.get("hostname"));
        assertNotNull(node.get("attributes"));

        assertNotNull(node.get("indices"));
        assertNotNull(node.get("jvm"));

        assertEquals(7, node.entrySet().size());
    }

}
