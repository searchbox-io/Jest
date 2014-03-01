package io.searchbox.cluster;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;
import org.mockito.internal.matchers.GreaterOrEqual;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 2)
public class NodesStatsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void nodesStatsAllWithClear() throws IOException {
        JestResult result = client.execute(new NodesStats.Builder()
                .clear(true)
                .build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        assertThat("At least 2 nodes expected in stats response", nodeEntries.size(), new GreaterOrEqual<Integer>(2));

        for (Map.Entry<String, JsonElement> nodeEntry : nodeEntries) {
            JsonObject node = nodes.getAsJsonObject(nodeEntry.getKey());
            assertNotNull(node);

            // check for the default node stats
            assertNotNull(node.get("timestamp"));
            assertNotNull(node.get("name"));
            assertNotNull(node.get("transport_address"));
            assertNotNull(node.get("host"));
            assertNotNull(node.get("ip"));

            // node stats should only contain the default stats as we set clear=true
            assertEquals(5, node.entrySet().size());
        }
    }

    @Test
    public void nodesStatsWithClear() throws IOException {
        JestResult result = client.execute(new NodesStats.Builder()
                .addNode("node_0")
                .clear(true)
                .build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        assertEquals("only 1 node expected in stats response", 1, nodeEntries.size());
        JsonObject node = nodes.getAsJsonObject(nodeEntries.iterator().next().getKey());
        assertNotNull(node);

        // check for the default node stats
        assertNotNull(node.get("timestamp"));
        assertNotNull(node.get("name"));
        assertNotNull(node.get("transport_address"));
        assertNotNull(node.get("host"));
        assertNotNull(node.get("ip"));

        // node stats should only contain the default stats as we set clear=true
        assertEquals(5, node.entrySet().size());
    }

    @Test
    public void nodesStatsWithClearAndIndices() throws IOException {
        JestResult result = client.execute(new NodesStats.Builder()
                .addNode("node_0")
                .clear(true)
                .indices(true)
                .build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        assertEquals("only 1 node expected in stats response", 1, nodeEntries.size());
        JsonObject node = nodes.getAsJsonObject(nodeEntries.iterator().next().getKey());
        assertNotNull(node);

        // check for the default node stats
        assertNotNull(node.get("timestamp"));
        assertNotNull(node.get("name"));
        assertNotNull(node.get("transport_address"));
        assertNotNull(node.get("host"));
        assertNotNull(node.get("ip"));

        assertNotNull(node.get("indices"));

        assertEquals(6, node.entrySet().size());
    }

    @Test
    public void nodesStatsWithClearAndIndicesAndJvm() throws IOException {
        JestResult result = client.execute(new NodesStats.Builder()
                .addNode("node_0")
                .clear(true)
                .indices(true)
                .jvm(true)
                .build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        assertEquals("only 1 node expected in stats response", 1, nodeEntries.size());
        JsonObject node = nodes.getAsJsonObject(nodeEntries.iterator().next().getKey());
        assertNotNull(node);

        // check for the default node stats
        assertNotNull(node.get("timestamp"));
        assertNotNull(node.get("name"));
        assertNotNull(node.get("transport_address"));
        assertNotNull(node.get("host"));
        assertNotNull(node.get("ip"));

        assertNotNull(node.get("indices"));
        assertNotNull(node.get("jvm"));

        assertEquals(7, node.entrySet().size());
    }

}
