package io.searchbox.indices.type;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.PutMapping;

import java.io.IOException;

import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class TypeExistIntegrationTest extends AbstractIntegrationTest {

	static final String INDEX_NAME = "happypg";
	static final String INDEX_TYPE = "seohoo";

	@Before
	public void setup() {
		createIndex(INDEX_NAME);
	}

	@Test
	public void indexTypeExists() throws IOException {

		createType();

		Action build = new TypeExist.Builder(INDEX_NAME).addType(INDEX_TYPE).build();
		JestResult result = client.execute(build);

		assertNotNull(result);
		assertTrue(result.isSucceeded());
	}

	@Test
	public void indexTypeNotExists() throws IOException {
		Action build = new TypeExist.Builder(INDEX_NAME).addType(INDEX_TYPE).build();

		JestResult result = client.execute(build);
		assertNotNull(result);
		assertFalse(result.isSucceeded());
	}

	private void createType() throws IOException {

		PutMapping putMapping = new PutMapping.Builder(INDEX_NAME,
														INDEX_TYPE,
														"{ \"document\" : { \"properties\" : { \"message\" : {\"type\" : \"string\", \"store\" : \"yes\"} } } }").build();
		JestResult result = client.execute(putMapping);
		assertNotNull(result);
		assertTrue(result.isSucceeded());
	}
}
