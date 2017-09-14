package io.searchbox.cluster;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Equals;
import org.mockito.internal.matchers.GreaterOrEqual;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class NodesStatsIntegrationTest extends AbstractIntegrationTest {
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        ensureFullyConnectedCluster();
    }

    @Test
    public void nodesStatsAll() throws IOException {
        JestResult result = client.execute(new NodesStats.Builder()
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        final int numNodes = internalCluster().getNodeNames().length;
        assertEquals("All nodes expected in stats response", numNodes, nodeEntries.size());

        int numDataNodes = 0;
        for (Map.Entry<String, JsonElement> nodeEntry : nodeEntries) {
            JsonObject node = nodeEntry.getValue().getAsJsonObject();
            assertNotNull(node);

            if (node.getAsJsonArray("roles").contains(new JsonPrimitive("data"))) {
                // check for the default node stats
                assertNotNull(node.get("timestamp"));
                assertNotNull(node.get("name"));
                assertNotNull(node.get("transport_address"));
                assertNotNull(node.get("host"));
                assertNotNull(node.get("ip"));

                numDataNodes++;
            }
        }
        assertEquals(1, numDataNodes);
    }

    @Test
    public void nodesStats() throws IOException {
        String dataNode = internalCluster().startDataOnlyNode();

        JestResult result = client.execute(new NodesStats.Builder()
                .addNode(dataNode)
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        assertEquals("Only 1 node expected in stats response", 1, nodeEntries.size());

        int numDataNodes = 0;
        for (Map.Entry<String, JsonElement> nodeEntry : nodeEntries) {
            JsonObject node = nodeEntry.getValue().getAsJsonObject();
            assertNotNull(node);

            if (node.getAsJsonArray("roles").contains(new JsonPrimitive("data"))) {
                // check for the default node stats
                assertNotNull(node.get("timestamp"));
                assertNotNull(node.get("name"));
                assertNotNull(node.get("transport_address"));
                assertNotNull(node.get("host"));
                assertNotNull(node.get("ip"));
                
                numDataNodes++;
            }
        }
        assertEquals(1, numDataNodes);
    }

    @Test
    public void nodesStatsWithIndices() throws IOException {
        String dataNode = internalCluster().startDataOnlyNode();

        JestResult result = client.execute(new NodesStats.Builder()
                .addNode(dataNode)
                .withIndices()
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        assertEquals("Only 1 node expected in stats response", 1, nodeEntries.size());

        int numDataNodes = 0;
        for (Map.Entry<String, JsonElement> nodeEntry : nodeEntries) {
            JsonObject node = nodeEntry.getValue().getAsJsonObject();
            assertNotNull(node);

            if (node.getAsJsonArray("roles").contains(new JsonPrimitive("data"))) {
                // check for the default node stats
                assertNotNull(node.get("timestamp"));
                assertNotNull(node.get("name"));
                assertNotNull(node.get("transport_address"));
                assertNotNull(node.get("host"));
                assertNotNull(node.get("ip"));
                assertNotNull(node.get("indices"));

                // node stats should only contain the default stats as we set clear=true
                assertEquals(7, node.entrySet().size());
                numDataNodes++;
            }
        }
        assertEquals(1, numDataNodes);
    }

    @Test
    public void nodesStatsWithIndicesAndJvm() throws IOException {
        String dataNode = internalCluster().startDataOnlyNode();

        JestResult result = client.execute(new NodesStats.Builder()
                .addNode(dataNode)
                .withIndices()
                .withJvm()
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        assertEquals("Only 1 node expected in stats response", 1, nodeEntries.size());

        int numDataNodes = 0;
        for (Map.Entry<String, JsonElement> nodeEntry : nodeEntries) {
            JsonObject node = nodeEntry.getValue().getAsJsonObject();
            assertNotNull(node);

            if (node.getAsJsonArray("roles").contains(new JsonPrimitive("data"))) {
                // check for the default node stats
                assertNotNull(node.get("timestamp"));
                assertNotNull(node.get("name"));
                assertNotNull(node.get("transport_address"));
                assertNotNull(node.get("host"));
                assertNotNull(node.get("ip"));
                assertNotNull(node.get("indices"));
                assertNotNull(node.get("jvm"));

                // node stats should only contain the default stats as we set clear=true
                assertEquals(8, node.entrySet().size());
                numDataNodes++;
            }
        }
        assertEquals(1, numDataNodes);
    }

}
