package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class RefreshIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testFlushAll() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        createIndex("i_flush_0", "i_flush_1", "i_flush_2");

        Refresh refresh = new Refresh.Builder().build();
        JestResult result = client.execute(refresh);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true));
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);

        // Each index will have 1 refresh op counted at creation and 1 at the explicit refresh request
        assertEquals(2, statsResponse.getIndex("i_flush_0").getTotal().getRefresh().getTotal());
        assertEquals(2, statsResponse.getIndex("i_flush_1").getTotal().getRefresh().getTotal());
        assertEquals(2, statsResponse.getIndex("i_flush_2").getTotal().getRefresh().getTotal());
    }

    @Test
    public void testFlushSpecificIndices() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        createIndex("i_flush_4", "i_flush_5", "i_flush_6");

        Refresh refresh = new Refresh.Builder().addIndex("i_flush_4").addIndex("i_flush_6").build();
        JestResult result = client.execute(refresh);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true));
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);

        // Each index will have 1 refresh op counted at creation and 1 at the explicit refresh request
        assertEquals(2, statsResponse.getIndex("i_flush_4").getTotal().getRefresh().getTotal());
        assertEquals(1, statsResponse.getIndex("i_flush_5").getTotal().getRefresh().getTotal());
        assertEquals(2, statsResponse.getIndex("i_flush_6").getTotal().getRefresh().getTotal());
    }

}
