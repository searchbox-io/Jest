package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;

/**
 * @author ferhat sobay
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class DeleteIndexIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void deleteIndex() throws IOException {
        String indexName = "newindex";
        createIndex(indexName);

        DeleteIndex indicesExists = new DeleteIndex.Builder(indexName).build();
        JestResult result = client.execute(indicesExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void deleteNonExistingIndex() throws IOException {
        DeleteIndex deleteIndex = new DeleteIndex.Builder("newindex2").build();
        JestResult result = client.execute(deleteIndex);
        assertFalse("Delete request should fail for an index that does not exist", result.isSucceeded());
    }

}
