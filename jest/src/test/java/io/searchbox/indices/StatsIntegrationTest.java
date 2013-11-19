package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class StatsIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "flush_test_index";

    @Test
    @ElasticsearchIndex(indexName = INDEX_NAME)
    public void testDefaultStats() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Stats stats = new Stats.Builder().build();
        JestResult result = client.execute(stats);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

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
