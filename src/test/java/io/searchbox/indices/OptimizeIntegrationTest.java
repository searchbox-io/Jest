package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.*;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
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
public class OptimizeIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "flush_test_index";
    @ElasticsearchAdminClient
    AdminClient adminClient;
    @ElasticsearchClient
    Client directClient;

    // TODO find a way to confirm a previous optimize request on server
    @Ignore
    @Test
    @ElasticsearchIndex(
            indexName = INDEX_NAME,
            settings = {@ElasticsearchSetting(name = "number_of_shards", value = "1")}
    )
    public void testOptimizeDefault() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Optimize optimize = new Optimize.Builder().maxNumSegments(1).build();
        JestResult result = client.execute(optimize);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        IndicesStatsResponse statsResponse = adminClient.indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true)).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);
        assertNotNull(statsResponse.getTotal().getMerge());
        assertEquals(1, statsResponse.getTotal().getMerge().getTotal());
    }
}
