package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author ferhat
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class PutMappingIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "mapping_index";
    private static final String INDEX_TYPE = "document";

    @Before
    public void setup() {
        createIndex(INDEX_NAME);
        ensureSearchable(INDEX_NAME);
    }

    @Test
    public void testPutMapping() throws IOException {
        PutMapping putMapping = new PutMapping.Builder(
                INDEX_NAME,
                INDEX_TYPE,
                "{ \"document\" : { \"properties\" : { \"message\" : {\"type\" : \"string\", \"store\" : \"yes\"} } } }"
        ).build();

        JestResult result = client.execute(putMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testPutMappingWithDocumentMapperBuilder() throws IOException {
        RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder(INDEX_TYPE).add(
                new StringFieldMapper.Builder("message").store(true)
        );

        GetSettingsResponse getSettingsResponse =
                client().admin().indices().getSettings(new GetSettingsRequest().indices(INDEX_NAME)).actionGet();
        DocumentMapper documentMapper = new DocumentMapper
                .Builder(INDEX_NAME, getSettingsResponse.getIndexToSettings().get(INDEX_NAME), rootObjectMapperBuilder).build(null);
        String expectedMappingSource = documentMapper.mappingSource().toString();
        PutMapping putMapping = new PutMapping.Builder(
                INDEX_NAME,
                INDEX_TYPE,
                expectedMappingSource
        ).build();

        JestResult result = client.execute(putMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

}
