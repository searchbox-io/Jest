package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.*;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

/**
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class FlushIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "flush_test_index";
    private static final String INDEX_NAME_2 = "flush_test_index_2";
    private static final String INDEX_NAME_3 = "flush_test_index_3";
    @ElasticsearchAdminClient
    AdminClient adminClient;
    @ElasticsearchClient
    Client directClient;

    @Test
    @ElasticsearchIndex(indexName = INDEX_NAME)
    public void testFlushAll() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Flush flush = new Flush.Builder().build();
        JestResult result = client.execute(flush);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = adminClient.indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true));
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);
        assertEquals("There should be exactly one flush operation performed on this index",
                1, statsResponse.getIndex(INDEX_NAME).getTotal().getFlush().getTotal());
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME),
            @ElasticsearchIndex(indexName = INDEX_NAME_2),
            @ElasticsearchIndex(indexName = INDEX_NAME_3)
    })
    public void testFlushSpecificIndices() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Flush flush = new Flush.Builder().addIndex(INDEX_NAME_2).addIndex(INDEX_NAME_3).build();
        JestResult result = client.execute(flush);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = adminClient.indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true));
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);
        assertEquals("There should not be any flush operation performed on this index",
                0, statsResponse.getIndex(INDEX_NAME).getTotal().getFlush().getTotal());
        assertEquals("There should be exactly one flush operation performed on this index",
                1, statsResponse.getIndex(INDEX_NAME_2).getTotal().getFlush().getTotal());
        assertEquals("There should be exactly one flush operation performed on this index",
                1, statsResponse.getIndex(INDEX_NAME_3).getTotal().getFlush().getTotal());
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
    @ElasticsearchIndex(indexName = INDEX_NAME)
    public void testFlushAllWithRefresh() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Flush flush = new Flush.Builder().setParameter(Parameters.REFRESH, true).build();
        JestResult result = client.execute(flush);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = adminClient.indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true));
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);
        assertEquals("There should be exactly one flush operation performed on this index",
                1, statsResponse.getIndex(INDEX_NAME).getTotal().getFlush().getTotal());
        assertEquals("Index should be refreshed when explicitly specified",
                2, statsResponse.getIndex(INDEX_NAME).getTotal().getRefresh().getTotal());
    }

}
