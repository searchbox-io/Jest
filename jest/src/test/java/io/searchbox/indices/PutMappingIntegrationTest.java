package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.GetMapping;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author ferhat
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class PutMappingIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "mapping_index";
    private static final String INDEX_TYPE = "document";

    @Before
    public void setup() {
        createIndex(INDEX_NAME);
    }

    @Test
    public void testPutMapping() {
        PutMapping putMapping = new PutMapping.Builder(
                INDEX_NAME,
                INDEX_TYPE,
                "{ \"document\" : { \"properties\" : { \"message\" : {\"type\" : \"string\", \"store\" : \"yes\"} } } }"
        ).build();
        try {
            JestResult result = client.execute(putMapping);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }
    }

    @Test
    public void testPutMappingWithDocumentMapperBuilder() {
        RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder(INDEX_TYPE).add(
                new StringFieldMapper.Builder("message").store(true)
        );
        DocumentMapper documentMapper = new DocumentMapper.Builder(INDEX_NAME, null, rootObjectMapperBuilder).build(null);
        String expectedMappingSource = documentMapper.mappingSource().toString();
        PutMapping putMapping = new PutMapping.Builder(
                INDEX_NAME,
                INDEX_TYPE,
                expectedMappingSource
        ).build();
        try {
            JestResult result = client.execute(putMapping);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }

        try {
            JestResult result = client.execute(new GetMapping.Builder().addIndex(INDEX_NAME).addType(INDEX_TYPE).build());
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            assertTrue("Actual mapping JSON does not match with the expected mapping", result.getJsonString().contains(expectedMappingSource));
        } catch (IOException e) {
            fail("Test failed while retrieving mapping information");
        }
    }

}
