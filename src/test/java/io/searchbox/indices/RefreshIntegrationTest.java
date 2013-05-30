package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.*;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
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
public class RefreshIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "flush_test_index";
    private static final String INDEX_NAME_2 = "flush_test_index_2";
    private static final String INDEX_NAME_3 = "flush_test_index_3";
    @ElasticsearchAdminClient
    AdminClient adminClient;

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME),
            @ElasticsearchIndex(indexName = INDEX_NAME_2),
            @ElasticsearchIndex(indexName = INDEX_NAME_3)
    })
    public void testFlushAll() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Refresh refresh = new Refresh.Builder().build();
        JestResult result = client.execute(refresh);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = adminClient.indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true));
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);

        // Each index will have 1 refresh op counted at creation and 1 at the explicit refresh request
        assertEquals(2, statsResponse.getIndex(INDEX_NAME).getTotal().getRefresh().getTotal());
        assertEquals(2, statsResponse.getIndex(INDEX_NAME_2).getTotal().getRefresh().getTotal());
        assertEquals(2, statsResponse.getIndex(INDEX_NAME_3).getTotal().getRefresh().getTotal());
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME),
            @ElasticsearchIndex(indexName = INDEX_NAME_2),
            @ElasticsearchIndex(indexName = INDEX_NAME_3)
    })
    public void testFlushSpecificIndices() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Refresh refresh = new Refresh.Builder().addIndex(INDEX_NAME).addIndex(INDEX_NAME_3).build();
        JestResult result = client.execute(refresh);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = adminClient.indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true));
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);

        // Each index will have 1 refresh op counted at creation and 1 at the explicit refresh request
        assertEquals(2, statsResponse.getIndex(INDEX_NAME).getTotal().getRefresh().getTotal());
        assertEquals(1, statsResponse.getIndex(INDEX_NAME_2).getTotal().getRefresh().getTotal());
        assertEquals(2, statsResponse.getIndex(INDEX_NAME_3).getTotal().getRefresh().getTotal());
    }

}
