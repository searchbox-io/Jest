package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class RefreshIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testFlushAll() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        createIndex("i_flush_0", "i_flush_1", "i_flush_2");
        ensureSearchable("i_flush_0", "i_flush_1", "i_flush_2");

        Refresh refresh = new Refresh.Builder().build();
        JestResult result = client.execute(refresh);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true));
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);

        IndexStats stats0 = statsResponse.getIndex("i_flush_0");
        assertEquals(stats0.getShards().length, stats0.getTotal().getRefresh().getTotal());

        IndexStats stats1 = statsResponse.getIndex("i_flush_1");
        assertEquals(stats1.getShards().length, stats1.getTotal().getRefresh().getTotal());

        IndexStats stats2 = statsResponse.getIndex("i_flush_2");
        assertEquals(stats2.getShards().length, stats2.getTotal().getRefresh().getTotal());
    }

    @Test
    public void testFlushSpecificIndices() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        createIndex("i_flush_4", "i_flush_5", "i_flush_6");
        ensureSearchable("i_flush_4", "i_flush_5", "i_flush_6");

        Refresh refresh = new Refresh.Builder().addIndex("i_flush_4").addIndex("i_flush_6").build();
        JestResult result = client.execute(refresh);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true));
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);

        IndexStats stats4 = statsResponse.getIndex("i_flush_4");
        assertEquals(stats4.getShards().length, stats4.getTotal().getRefresh().getTotal());

        IndexStats stats5 = statsResponse.getIndex("i_flush_5");
        assertEquals(0, stats5.getTotal().getRefresh().getTotal());

        IndexStats stats6 = statsResponse.getIndex("i_flush_6");
        assertEquals(stats6.getShards().length, stats6.getTotal().getRefresh().getTotal());
    }

}
