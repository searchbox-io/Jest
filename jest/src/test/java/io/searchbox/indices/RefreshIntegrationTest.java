package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class RefreshIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testFlushAll() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        createIndex("i_flush_0", Settings.builder().put("number_of_shards", 1).put("number_of_replicas", 0).build());
        createIndex("i_flush_1", Settings.builder().put("number_of_shards", 5).put("number_of_replicas", 0).build());
        createIndex("i_flush_2", Settings.builder().put("number_of_shards", 6).put("number_of_replicas", 0).build());
        ensureSearchable("i_flush_0", "i_flush_1", "i_flush_2");


        Refresh refresh = new Refresh.Builder().build();
        JestResult result = client.execute(refresh);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        assertEquals(12, result.getJsonObject().get("_shards").getAsJsonObject().get("total").getAsInt());

    }

    @Test
    public void testFlushSpecificIndices() throws IOException {
        createIndex("i_flush_4", Settings.builder().put("number_of_shards", 1).put("number_of_replicas", 0).build());
        createIndex("i_flush_5", Settings.builder().put("number_of_shards", 5).put("number_of_replicas", 0).build());
        createIndex("i_flush_6", Settings.builder().put("number_of_shards", 6).put("number_of_replicas", 0).build());
        ensureSearchable("i_flush_4", "i_flush_5", "i_flush_6");

        Refresh refresh = new Refresh.Builder().addIndex("i_flush_4").addIndex("i_flush_6").build();
        JestResult result = client.execute(refresh);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(7, result.getJsonObject().get("_shards").getAsJsonObject().get("total").getAsInt());
    }

}
