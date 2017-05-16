package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 2)
public class FlushIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "flush_test_index";
    private static final String INDEX_NAME_2 = "flush_test_index_2";
    private static final String INDEX_NAME_3 = "flush_test_index_3";

    @Before
    public void setup() {
        createIndex(INDEX_NAME, INDEX_NAME_2, INDEX_NAME_3);
        ensureSearchable(INDEX_NAME, INDEX_NAME_2, INDEX_NAME_3);
    }

    @Test
    public void testFlushAll() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Flush flush = new Flush.Builder().build();
        JestResult result = client.execute(flush);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        IndicesStatsResponse statsResponse = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true)).actionGet();
        assertNotNull(statsResponse);
        IndexStats stats1 = statsResponse.getIndex(INDEX_NAME);

        assertEquals("There should be exactly one flush operation per shard performed on this index",
                stats1.getShards().length, stats1.getTotal().getFlush().getTotal());
    }

    @Test
    public void testFlushWithForce() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Flush flush = new Flush.Builder().force().build();
        JestResult result = client.execute(flush);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        IndicesStatsResponse statsResponse = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true)).actionGet();
        assertNotNull(statsResponse);
        IndexStats stats1 = statsResponse.getIndex(INDEX_NAME);

        assertEquals("There should be exactly one flush operation per shard performed on this index",
                stats1.getShards().length, stats1.getTotal().getFlush().getTotal());
    }

    @Test
    public void testFlushWithWaitifOngoing() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Flush flush = new Flush.Builder().waitIfOngoing().build();
        JestResult result = client.execute(flush);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        IndicesStatsResponse statsResponse = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true)).actionGet();
        assertNotNull(statsResponse);
        IndexStats stats1 = statsResponse.getIndex(INDEX_NAME);

        assertEquals("There should be exactly one flush operation per shard performed on this index",
                stats1.getShards().length, stats1.getTotal().getFlush().getTotal());
    }

    @Test
    public void testFlushSpecificIndices() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Flush flush = new Flush.Builder().addIndex(INDEX_NAME_2).addIndex(INDEX_NAME_3).build();
        JestResult result = client.execute(flush);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        IndicesStatsResponse statsResponse = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true)).actionGet();
        assertNotNull(statsResponse);
        IndexStats stats1 = statsResponse.getIndex(INDEX_NAME);
        IndexStats stats2 = statsResponse.getIndex(INDEX_NAME_2);
        IndexStats stats3 = statsResponse.getIndex(INDEX_NAME_3);

        assertEquals("There should not be any flush operation performed on first index",
                0, stats1.getTotal().getFlush().getTotal());
        assertEquals("There should be exactly one flush operation per shard performed on second index",
                stats2.getShards().length, stats2.getTotal().getFlush().getTotal());
        assertEquals("There should be exactly one flush operation per shard performed on third index",
                stats3.getShards().length, stats3.getTotal().getFlush().getTotal());
    }

}
