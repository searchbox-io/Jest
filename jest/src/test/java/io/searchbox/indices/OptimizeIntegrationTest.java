package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class OptimizeIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "flush_test_index";

    // TODO find a way to confirm a previous optimize request on server
    @Ignore
    @Test
    public void testOptimizeDefault() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Optimize optimize = new Optimize.Builder().maxNumSegments(1).build();
        JestResult result = client.execute(optimize);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        IndicesStatsResponse statsResponse = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true)).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);
        assertNotNull(statsResponse.getTotal().getMerge());
        assertEquals(1, statsResponse.getTotal().getMerge().getTotal());
    }
}
