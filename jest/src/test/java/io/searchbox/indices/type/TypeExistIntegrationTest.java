package io.searchbox.indices.type;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 1)
public class TypeExistIntegrationTest extends AbstractIntegrationTest {

    static final String INDEX_NAME = "it_typexst_0";
    static final String INDEX_TYPE = "ittyp";

    @Before
    public void setup() {
        createIndex(INDEX_NAME);
        ensureSearchable(INDEX_NAME);
    }

    @Test
    public void indexTypeExists() throws IOException, InterruptedException {
        IndexResponse indexResponse = client().index(
                new IndexRequest(INDEX_NAME, INDEX_TYPE)
                        .source("{\"user\":\"tweety\"}")
                        .refresh(true)
        ).actionGet();
        assertTrue(indexResponse.isCreated());

        Action typeExist = new TypeExist.Builder(INDEX_NAME).addType(INDEX_TYPE).build();
        JestResult result = client.execute(typeExist);

        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void indexTypeNotExists() throws IOException {
        Action typeExist = new TypeExist.Builder(INDEX_NAME).addType(INDEX_TYPE).build();

        JestResult result = client.execute(typeExist);
        assertFalse(result.isSucceeded());
    }

}
