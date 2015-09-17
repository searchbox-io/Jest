package io.searchbox.cluster;

import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class StateIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void clusterState() throws IOException {
        String index1 = "a1";
        String index2 = "b2";
        String index3 = "c3";

        createIndex(index1, index2, index3);
        ensureSearchable(index1, index2, index3);

        JestResult result = client.execute(new State.Builder().indices(index2).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject resultJson = result.getJsonObject();
        assertNotNull(resultJson);
        assertNotNull(resultJson.getAsJsonObject("nodes"));
        assertNotNull(resultJson.getAsJsonObject("routing_table"));
        assertNotNull(resultJson.getAsJsonObject("blocks"));

        JsonObject metadata = resultJson.getAsJsonObject("metadata");
        assertNotNull(metadata);
        JsonObject indices = metadata.getAsJsonObject("indices");
        assertFalse(indices.has(index1));
        assertTrue(indices.has(index2));
        assertFalse(indices.has(index3));
    }

    @Test
    public void clusterStateWithMetadata() throws IOException {
        JestResult result = client.execute(new State.Builder().withMetadata().build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject resultJson = result.getJsonObject();
        assertNotNull(resultJson);
        assertNull(resultJson.getAsJsonObject("nodes"));
        assertNull(resultJson.getAsJsonObject("routing_table"));
        assertNotNull(resultJson.getAsJsonObject("metadata"));
        assertNull(resultJson.getAsJsonObject("blocks"));
    }

}
