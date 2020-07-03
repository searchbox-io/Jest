package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 2)
public class OpenIndexIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX_NAME = "test_index";
    private static final String INDEX_NAME_2 = "test_index_2";

    @Test
    public void testOpen() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        createIndex(INDEX_NAME, INDEX_NAME_2);
        ensureGreen();

        ActionFuture<AcknowledgedResponse> closeIndexResponseActionFuture = client().admin().indices().close(
                new CloseIndexRequest(INDEX_NAME_2));
        AcknowledgedResponse closeIndexResponse = closeIndexResponseActionFuture.actionGet(10, TimeUnit.SECONDS);
        assertNotNull(closeIndexResponse);
        assertTrue(closeIndexResponse.isAcknowledged());

        assertEquals(
                "There should be 1 index at the start",
                1,
                client().admin().indices().stats(new IndicesStatsRequest()).actionGet().getIndices().size()
        );

        OpenIndex openIndex = new OpenIndex.Builder(INDEX_NAME_2).build();
        JestResult result = client.execute(openIndex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        ensureGreen(INDEX_NAME_2);

        assertEquals(
                "There should be 2 indices after open operation",
                2,
                client().admin().indices().stats(new IndicesStatsRequest()).actionGet().getIndices().size()
        );
    }
}
