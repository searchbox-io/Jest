package io.searchbox.cluster;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;
import org.mockito.internal.matchers.GreaterOrEqual;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 2)
public class NodesStatsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void nodesStatsAllWithClear() throws IOException {
        JestResult result = client.execute(new NodesStats.Builder()
                .withClear()
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        assertThat("At least 2 nodes expected in stats response", nodeEntries.size(), new GreaterOrEqual<Integer>(2));

        int numDataNodes = 0;
        for (Map.Entry<String, JsonElement> nodeEntry : nodeEntries) {
            JsonObject node = nodeEntry.getValue().getAsJsonObject();
            assertNotNull(node);

            // if it has attributes then it's not a default data note and we're not interested in those
            if (node.getAsJsonObject("attributes").entrySet().size() < 3) {
                // check for the default node stats
                assertNotNull(node.get("timestamp"));
                assertNotNull(node.get("name"));
                assertNotNull(node.get("transport_address"));
                assertNotNull(node.get("host"));
                assertNotNull(node.get("ip"));

                // node stats should only contain the default stats as we set clear=true
                assertEquals(6, node.entrySet().size());
                numDataNodes++;
            }
        }
        assertEquals(2, numDataNodes);
    }

    @Test
    public void nodesStatsWithClear() throws IOException {
        String firstNode = internalCluster().getNodeNames()[0];

        JestResult result = client.execute(new NodesStats.Builder()
                .addNode(firstNode)
                .withClear()
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        assertThat("At least 1 node expected in stats response", nodeEntries.size(), new GreaterOrEqual<Integer>(1));

        int numDataNodes = 0;
        for (Map.Entry<String, JsonElement> nodeEntry : nodeEntries) {
            JsonObject node = nodeEntry.getValue().getAsJsonObject();
            assertNotNull(node);

            // if it has attributes then it's not a default data note and we're not interested in those
            if (node.getAsJsonObject("attributes").entrySet().size() < 3) {
                // check for the default node stats
                assertNotNull(node.get("timestamp"));
                assertNotNull(node.get("name"));
                assertNotNull(node.get("transport_address"));
                assertNotNull(node.get("host"));
                assertNotNull(node.get("ip"));

                // node stats should only contain the default stats as we set clear=true
                assertEquals(6, node.entrySet().size());
                numDataNodes++;
            }
        }
        assertEquals(1, numDataNodes);
    }

    @Test
    public void nodesStatsWithClearAndIndices() throws IOException {
        String firstNode = internalCluster().getNodeNames()[0];

        JestResult result = client.execute(new NodesStats.Builder()
                .addNode(firstNode)
                .withClear()
                .withIndices()
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        assertThat("At least 1 node expected in stats response", nodeEntries.size(), new GreaterOrEqual<Integer>(1));

        int numDataNodes = 0;
        for (Map.Entry<String, JsonElement> nodeEntry : nodeEntries) {
            JsonObject node = nodeEntry.getValue().getAsJsonObject();
            assertNotNull(node);

            // if it has attributes then it's not a default data note and we're not interested in those
            if (node.getAsJsonObject("attributes").entrySet().size() < 3) {
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
    public void nodesStatsWithClearAndIndicesAndJvm() throws IOException {
        String firstNode = internalCluster().getNodeNames()[0];

        JestResult result = client.execute(new NodesStats.Builder()
                .addNode(firstNode)
                .withClear()
                .withIndices()
                .withJvm()
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject nodes = result.getJsonObject().getAsJsonObject("nodes");
        assertNotNull(nodes);
        Set<Map.Entry<String, JsonElement>> nodeEntries = nodes.entrySet();
        assertThat("At least 1 node expected in stats response", nodeEntries.size(), new GreaterOrEqual<Integer>(1));

        int numDataNodes = 0;
        for (Map.Entry<String, JsonElement> nodeEntry : nodeEntries) {
            JsonObject node = nodeEntry.getValue().getAsJsonObject();
            assertNotNull(node);

            // if it has attributes then it's not a default data note and we're not interested in those
            if (node.getAsJsonObject("attributes").entrySet().size() < 3) {
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
