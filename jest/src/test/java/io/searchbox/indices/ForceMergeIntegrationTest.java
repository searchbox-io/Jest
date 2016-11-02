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
public class ForceMergeIntegrationTest extends AbstractIntegrationTest {

    // TODO find a way to confirm a previous merge request on server
    @Ignore
    @Test
    public void testForceMergeDefault() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        ForceMerge forceMerge = new ForceMerge.Builder().maxNumSegments(1).build();
        JestResult result = client.execute(forceMerge);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        IndicesStatsResponse statsResponse = client().admin().indices().stats(
                new IndicesStatsRequest().clear().flush(true).refresh(true)).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(statsResponse);
        assertNotNull(statsResponse.getTotal().getMerge());
        assertEquals(1, statsResponse.getTotal().getMerge().getTotal());
    }
}
