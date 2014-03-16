package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numNodes = 2)
public class FlushIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "flush_test_index";
    private static final String INDEX_NAME_2 = "flush_test_index_2";
    private static final String INDEX_NAME_3 = "flush_test_index_3";

    @Before
    public void setup() {
        createIndex(INDEX_NAME, INDEX_NAME_2, INDEX_NAME_3);
        ensureGreen(INDEX_NAME, INDEX_NAME_2, INDEX_NAME_3);
    }

    @Test
    public void testFlushAll() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Flush flush = new Flush.Builder().build();
        JestResult result = client.execute(flush);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        IndicesStatsResponse statsResponse = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true)).actionGet();
        assertNotNull(statsResponse);
        assertEquals("There should be exactly one flush operation per node performed on this index",
                2, statsResponse.getIndex(INDEX_NAME).getTotal().getFlush().getTotal());
    }

    @Test
    public void testFlushSpecificIndices() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Flush flush = new Flush.Builder().addIndex(INDEX_NAME_2).addIndex(INDEX_NAME_3).build();
        JestResult result = client.execute(flush);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        IndicesStatsResponse statsResponse = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true)).actionGet();
        assertNotNull(statsResponse);
        assertEquals("There should not be any flush operation performed on first index",
                0, statsResponse.getIndex(INDEX_NAME).getTotal().getFlush().getTotal());
        assertEquals("There should be exactly one flush operation performed on second index",
                2, statsResponse.getIndex(INDEX_NAME_2).getTotal().getFlush().getTotal());
        assertEquals("There should be exactly one flush operation performed on third index",
                2, statsResponse.getIndex(INDEX_NAME_3).getTotal().getFlush().getTotal());
    }

    /**
     * This test fails as I was not able to find a way to confirm
     * the refresh operation through stats api. It seems that the refresh
     * stats only count the explicit _refresh calls (even if a flush request
     * is sent via the native client, stats api does not count the refresh
     * triggered by that request - maybe that's by design).
     */
    @Ignore
    @Test
    public void testFlushAllWithRefresh() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Flush flush = new Flush.Builder().setParameter(Parameters.REFRESH, true).build();
        JestResult result = client.execute(flush);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true));
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);
        assertEquals("There should be exactly one flush operation per node performed on this index",
                2, statsResponse.getIndex(INDEX_NAME).getTotal().getFlush().getTotal());
        assertEquals("Index should be refreshed when explicitly specified",
                3, statsResponse.getIndex(INDEX_NAME).getTotal().getRefresh().getTotal());
    }

}
