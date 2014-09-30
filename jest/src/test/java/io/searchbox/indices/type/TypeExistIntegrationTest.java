package io.searchbox.indices.type;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
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
    public void indexTypeExists() throws IOException {
		createType();

		Action build = new TypeExist.Builder(INDEX_NAME).addType(INDEX_TYPE).build();
		JestResult result = client.execute(build);

		assertNotNull(result);
		assertTrue(result.getErrorMessage(), result.isSucceeded());
	}

	@Test
	public void indexTypeNotExists() throws IOException {
		Action build = new TypeExist.Builder(INDEX_NAME).addType(INDEX_TYPE).build();

		JestResult result = client.execute(build);
		assertNotNull(result);
		assertFalse(result.isSucceeded());
	}

	private void createType() throws IOException {
        IndexResponse indexResponse = client().index(new IndexRequest(
                INDEX_NAME,
                INDEX_TYPE,
                "1")
                .refresh(true)
                .source("{\"user\":\"tweety\"}"))
                .actionGet();
        assertTrue(indexResponse.isCreated());

        GetResponse getResponse = client().get(new GetRequest(INDEX_NAME, INDEX_TYPE, "1")).actionGet();
        assertTrue(getResponse.isExists());
    }
}
