package io.searchbox.indices;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author ferhat sobay
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class DeleteIndexIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void deleteIndex() throws IOException {
        String indexName = "newindex";
        createIndex(indexName);

        DeleteIndex indicesExists = new DeleteIndex.Builder(indexName).build();
        executeTestCase(indicesExists);
    }

    @Test
    public void deleteNonExistingIndex() throws IOException {
        DeleteIndex deleteIndex = new DeleteIndex.Builder("newindex2").build();
        JestResult result = client.execute(deleteIndex);
        assertNotNull(result);
        assertFalse(result.isSucceeded());
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }
}
