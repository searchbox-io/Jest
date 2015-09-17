package io.searchbox.indices;

import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class StatsIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "flush_test_index";

    @Test
    public void testDefaultStats() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        createIndex(INDEX_NAME);
        ensureSearchable(INDEX_NAME);

        Stats stats = new Stats.Builder().build();
        JestResult result = client.execute(stats);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        // confirm that response has all the default stats types
        JsonObject jsonResult = result.getJsonObject();
        JsonObject statsJson = jsonResult.getAsJsonObject("indices").getAsJsonObject(INDEX_NAME).getAsJsonObject("total");
        assertNotNull(statsJson);
        assertNotNull(statsJson.getAsJsonObject("docs"));
        assertNotNull(statsJson.getAsJsonObject("store"));
        assertNotNull(statsJson.getAsJsonObject("indexing"));
        assertNotNull(statsJson.getAsJsonObject("get"));
        assertNotNull(statsJson.getAsJsonObject("search"));
    }
}
