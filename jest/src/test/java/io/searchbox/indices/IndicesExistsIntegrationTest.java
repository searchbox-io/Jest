package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndexes;
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
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class IndicesExistsIntegrationTest extends AbstractIntegrationTest {

    static final String INDEX_1_NAME = "osman";
    static final String INDEX_2_NAME = "john";

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_1_NAME),
            @ElasticsearchIndex(indexName = INDEX_2_NAME)
    })
    public void multiIndexNotExists() throws IOException {
        Action action = new IndicesExists.Builder(INDEX_1_NAME).addIndex("asd").build();

        JestResult result = client.execute(action);
        assertNotNull(result);
        assertFalse(result.isSucceeded());
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_1_NAME),
            @ElasticsearchIndex(indexName = INDEX_2_NAME)
    })
    public void multiIndexExists() throws IOException {
        Action action = new IndicesExists.Builder(INDEX_1_NAME).addIndex(INDEX_2_NAME).build();

        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    @ElasticsearchIndex(indexName = INDEX_1_NAME)
    public void indexExists() throws IOException {
        Action action = new IndicesExists.Builder(INDEX_1_NAME).build();

        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void indexNotExists() throws IOException {
        Action action = new IndicesExists.Builder(INDEX_1_NAME).build();

        JestResult result = client.execute(action);
        assertNotNull(result);
        assertFalse(result.isSucceeded());
    }

}
