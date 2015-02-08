package io.searchbox.indices;

import com.google.gson.JsonObject;
import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.GetMapping;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 1)
public class GetMappingIntegrationTest extends AbstractIntegrationTest {

    static final String INDEX_1_NAME = "book";
    static final String INDEX_2_NAME = "video";
    static final String CUSTOM_TYPE = "science-fiction";
    static final String DEFAULT_TYPE = "_default_";

    @Test
    public void testWithoutParameters() throws Exception {
        createIndex(INDEX_1_NAME, INDEX_2_NAME);

        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(
                new PutMappingRequest(INDEX_1_NAME)
                        .type(CUSTOM_TYPE)
                        .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"string\"}," +
                                "\"author\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());
        waitForConcreteMappingsOnAll(INDEX_1_NAME, CUSTOM_TYPE, "title", "author");

        GetMapping getMapping = new GetMapping.Builder().build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.isSucceeded());

        JsonObject resultJson = result.getJsonObject();
        JsonObject index1Object = resultJson.getAsJsonObject(INDEX_1_NAME);
        JsonObject index2Object = resultJson.getAsJsonObject(INDEX_2_NAME);

        assertNotNull("GetMapping response JSON should include the index " + INDEX_1_NAME, index1Object);
        assertNotNull("GetMapping response JSON should include the index " + INDEX_2_NAME, index2Object);

        JsonObject index1Mappings = index2Object.getAsJsonObject("mappings");
        assertEquals(1, index1Mappings.entrySet().size());
        assertNotNull(index1Mappings.get(DEFAULT_TYPE));

        JsonObject index2Mappings = index1Object.getAsJsonObject("mappings");
        assertEquals(2, index2Mappings.entrySet().size());
        assertNotNull(index2Mappings.get(DEFAULT_TYPE));
        assertNotNull(index2Mappings.get(CUSTOM_TYPE));
    }

    @Test
    public void testWithSingleIndex() throws Exception {
        createIndex(INDEX_1_NAME, INDEX_2_NAME);

        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(
                new PutMappingRequest(INDEX_1_NAME)
                        .type(CUSTOM_TYPE)
                        .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"string\"}," +
                                "\"author\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());
        waitForConcreteMappingsOnAll(INDEX_1_NAME, CUSTOM_TYPE, "title", "author");

        Action getMapping = new GetMapping.Builder().addIndex(INDEX_2_NAME).build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.isSucceeded());

        JsonObject resultJson = result.getJsonObject();
        JsonObject index2Object = resultJson.getAsJsonObject(INDEX_2_NAME);

        assertNotNull("GetMapping response JSON should include the index " + INDEX_2_NAME, index2Object);

        JsonObject mappings = index2Object.getAsJsonObject("mappings");
        assertEquals(1, mappings.entrySet().size());
        assertNotNull(mappings.get(DEFAULT_TYPE));
    }

    @Test
    public void testWithMultipleIndices() throws Exception {
        createIndex(INDEX_1_NAME, INDEX_2_NAME, "irrelevant");

        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(
                new PutMappingRequest(INDEX_1_NAME)
                        .type(CUSTOM_TYPE)
                        .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"string\"}," +
                                "\"author\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());
        putMappingResponse = client().admin().indices().putMapping(
                new PutMappingRequest(INDEX_2_NAME)
                        .type(CUSTOM_TYPE)
                        .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":false,\"type\":\"string\"}," +
                                "\"isbn\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());
        waitForConcreteMappingsOnAll(INDEX_1_NAME, CUSTOM_TYPE, "title", "author");
        waitForConcreteMappingsOnAll(INDEX_2_NAME, CUSTOM_TYPE, "title", "isbn");

        Action getMapping = new GetMapping.Builder().addIndex(INDEX_2_NAME).addIndex(INDEX_1_NAME).build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.isSucceeded());

        JsonObject resultJson = result.getJsonObject();
        JsonObject index1Object = resultJson.getAsJsonObject(INDEX_1_NAME);
        JsonObject index2Object = resultJson.getAsJsonObject(INDEX_2_NAME);

        assertNotNull("GetMapping response JSON should include the index " + INDEX_1_NAME, index1Object);
        assertNotNull("GetMapping response JSON should include the index " + INDEX_2_NAME, index2Object);

        JsonObject index2Mappings = index2Object.getAsJsonObject("mappings");
        assertEquals(2, index2Mappings.entrySet().size());
        assertNotNull(index2Mappings.get(DEFAULT_TYPE));
        assertNotNull(index2Mappings.get(CUSTOM_TYPE));

        JsonObject index1Mappings = index1Object.getAsJsonObject("mappings");
        assertEquals(2, index1Mappings.entrySet().size());
        assertNotNull(index1Mappings.get(DEFAULT_TYPE));
        assertNotNull(index1Mappings.get(CUSTOM_TYPE));
    }

}
