package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchAdminClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndexes;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.ActionFuture;
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
public class RefreshIntegrationTest extends AbstractIntegrationTest {

    @ElasticsearchAdminClient
    AdminClient adminClient;

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = "i_flush_0"),
            @ElasticsearchIndex(indexName = "i_flush_1"),
            @ElasticsearchIndex(indexName = "i_flush_2")
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
        assertEquals(2, statsResponse.getIndex("i_flush_0").getTotal().getRefresh().getTotal());
        assertEquals(2, statsResponse.getIndex("i_flush_1").getTotal().getRefresh().getTotal());
        assertEquals(2, statsResponse.getIndex("i_flush_2").getTotal().getRefresh().getTotal());
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = "i_flush_4"),
            @ElasticsearchIndex(indexName = "i_flush_5"),
            @ElasticsearchIndex(indexName = "i_flush_6")
    })
    public void testFlushSpecificIndices() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Refresh refresh = new Refresh.Builder().addIndex("i_flush_4").addIndex("i_flush_6").build();
        JestResult result = client.execute(refresh);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ActionFuture<IndicesStatsResponse> statsResponseFeature = adminClient.indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true));
        IndicesStatsResponse statsResponse = statsResponseFeature.get(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);

        // Each index will have 1 refresh op counted at creation and 1 at the explicit refresh request
        assertEquals(2, statsResponse.getIndex("i_flush_4").getTotal().getRefresh().getTotal());
        assertEquals(1, statsResponse.getIndex("i_flush_5").getTotal().getRefresh().getTotal());
        assertEquals(2, statsResponse.getIndex("i_flush_6").getTotal().getRefresh().getTotal());
    }

}
