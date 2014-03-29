package io.searchbox.indices;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class IndicesExistsIntegrationTest extends AbstractIntegrationTest {

    static final String INDEX_1_NAME = "osman";
    static final String INDEX_2_NAME = "john";

    @Before
    public void setup() {
        createIndex(INDEX_1_NAME, INDEX_2_NAME);
    }

    @Test
    public void multiIndexNotExists() throws IOException {
        Action action = new IndicesExists.Builder("qwe").addIndex("asd").build();

        JestResult result = client.execute(action);
        assertNotNull(result);
        assertFalse(result.isSucceeded());
    }

    @Test
    public void multiIndexExists() throws IOException {
        Action action = new IndicesExists.Builder(INDEX_1_NAME).addIndex(INDEX_2_NAME).build();

        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void indexExists() throws IOException {
        Action action = new IndicesExists.Builder(INDEX_1_NAME).build();

        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void indexNotExists() throws IOException {
        Action action = new IndicesExists.Builder("nope").build();

        JestResult result = client.execute(action);
        assertNotNull(result);
        assertFalse(result.isSucceeded());
    }

}
