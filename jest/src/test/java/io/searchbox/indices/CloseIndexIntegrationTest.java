package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 2)
public class CloseIndexIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX_NAME_1 = "test_index_1";
    private static final String INDEX_NAME_2 = "test_index_2";

    @Test
    public void testClose() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        createIndex(INDEX_NAME_1, INDEX_NAME_2);
        ensureGreen(INDEX_NAME_1, INDEX_NAME_2);

        assertEquals(
                "There should be 2 indices at the start",
                2,
                client().admin().indices().stats(new IndicesStatsRequest()).actionGet().getIndices().size()
        );

        CloseIndex closeIndex = new CloseIndex.Builder(INDEX_NAME_2).build();
        JestResult result = client.execute(closeIndex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        ensureGreen(INDEX_NAME_1);
        assertEquals(
                "There should be 1 index left after close operation",
                1,
                client().admin().indices().stats(new IndicesStatsRequest()).actionGet().getIndices().size()
        );
    }
}