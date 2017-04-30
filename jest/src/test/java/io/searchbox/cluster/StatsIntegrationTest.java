package io.searchbox.cluster;

import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class StatsIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void clusterStats() throws IOException {
        JestResult result = client.execute(new Stats.Builder().build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject resultJson = result.getJsonObject();
        assertNotNull(resultJson);
        assertNotNull(resultJson.getAsJsonPrimitive("timestamp"));
        assertNotNull(resultJson.getAsJsonPrimitive("cluster_name"));
        assertNotNull(resultJson.getAsJsonPrimitive("status"));
        assertNotNull(resultJson.getAsJsonObject("indices"));
        assertNotNull(resultJson.getAsJsonObject("nodes"));
        assertEquals(internalCluster().size(), resultJson.getAsJsonObject("nodes").getAsJsonObject("count").get("total").getAsInt());

    }

    @Test
    public void clusterStatsWithSpecificNodes() throws IOException {
        final String localNodeName = clusterService().localNode().getName();
        JestResult result = client.execute(new Stats.Builder().addNode(localNodeName).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject resultJson = result.getJsonObject();
        assertNotNull(resultJson);
        assertNotNull(resultJson.getAsJsonPrimitive("timestamp"));
        assertNotNull(resultJson.getAsJsonPrimitive("cluster_name"));
        assertNotNull(resultJson.getAsJsonObject("indices"));
        assertNotNull(resultJson.getAsJsonObject("nodes"));
        assertEquals(1, resultJson.getAsJsonObject("nodes").getAsJsonObject("count").get("total").getAsInt());
    }
}
