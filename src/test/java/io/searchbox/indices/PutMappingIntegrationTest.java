package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.GetMapping;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author ferhat
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class PutMappingIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "mapping_index";
    private static final String INDEX_TYPE = "document";

    @Test
    @ElasticsearchIndex(indexName = INDEX_NAME)
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
    @ElasticsearchIndex(indexName = INDEX_NAME)
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
            assertEquals("Actual mapping JSON does not match with the expected mapping", expectedMappingSource, result.getJsonString());
        } catch (IOException e) {
            fail("Test failed while retrieving mapping information");
        }
    }

}
