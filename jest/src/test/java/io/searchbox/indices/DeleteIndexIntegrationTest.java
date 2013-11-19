package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author ferhat sobay
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class DeleteIndexIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void deleteIndex() {

        String indexName = "newindex";

        CreateIndex createIndex = new CreateIndex.Builder(indexName).build();
        try {
            executeTestCase(createIndex);
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }

        DeleteIndex indicesExists = new DeleteIndex.Builder(indexName).build();
        try {
            executeTestCase(indicesExists);
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }
    }

    @Test
    public void deleteIndexNotExists() {
        DeleteIndex deleteIndex = new DeleteIndex.Builder("newindex2").build();
        try {
            JestResult result = client.execute(deleteIndex);
            assertNotNull(result);
            assertFalse(result.isSucceeded());
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }
}
