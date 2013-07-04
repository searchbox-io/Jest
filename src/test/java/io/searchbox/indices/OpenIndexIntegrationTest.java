package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchAdminClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndexes;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.AdminClient;
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
public class OpenIndexIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX_NAME = "test_index";
    private static final String INDEX_NAME_2 = "test_index_2";
    @ElasticsearchAdminClient
    AdminClient adminClient;

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME),
            @ElasticsearchIndex(indexName = INDEX_NAME_2)
    })
    public void testOpen() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        ActionFuture<CloseIndexResponse> closeIndexResponseActionFuture = adminClient.indices().close(
                new CloseIndexRequest(INDEX_NAME_2));
        CloseIndexResponse closeIndexResponse = closeIndexResponseActionFuture.actionGet(10, TimeUnit.SECONDS);
        assertNotNull(closeIndexResponse);
        assertTrue(closeIndexResponse.isAcknowledged());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = adminClient.indices().stats(
                new IndicesStatsRequest());
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);
        assertEquals("There should be only one shard open", 1, statsResponse.getTotalShards());

        OpenIndex openIndex = new OpenIndex.Builder(INDEX_NAME_2).build();
        JestResult result = client.execute(openIndex);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature2 = adminClient.indices().stats(
                new IndicesStatsRequest());
        IndicesStatsResponse statsResponse2 = statsResponseFeature2.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse2);
        assertEquals("All two shards should be open at this point", 2, statsResponse2.getTotalShards());
    }
}
