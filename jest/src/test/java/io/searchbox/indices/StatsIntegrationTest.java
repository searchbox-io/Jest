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
    private static final String STATS_WITH_OPTIONS_INDEX_NAME = "stats_with_options_index";

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

    @Test
    public void testStatsWithOptions() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        createIndex(STATS_WITH_OPTIONS_INDEX_NAME);
        ensureSearchable(STATS_WITH_OPTIONS_INDEX_NAME);

        Stats stats = new Stats.Builder()
                .flush(true)
                .indexing(true)
                .build();

        JestResult result = client.execute(stats);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        // Confirm that response has only flush and indexing stats types
        JsonObject jsonResult = result.getJsonObject();
        JsonObject statsJson = jsonResult.getAsJsonObject("indices").getAsJsonObject(STATS_WITH_OPTIONS_INDEX_NAME).getAsJsonObject("total");
        assertNotNull(statsJson);
        assertNotNull(statsJson.getAsJsonObject("flush"));
        assertNotNull(statsJson.getAsJsonObject("indexing"));
        assertEquals("Number of stats received", 2, statsJson.entrySet().size());
    }

}
