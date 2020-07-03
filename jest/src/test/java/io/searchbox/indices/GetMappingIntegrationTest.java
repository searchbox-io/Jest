package io.searchbox.indices;

import com.google.gson.JsonObject;
import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.GetMapping;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class GetMappingIntegrationTest extends AbstractIntegrationTest {

    static final String INDEX_1_NAME = "book";
    static final String INDEX_2_NAME = "video";
    static final String CUSTOM_TYPE = "science-fiction";

    @Test
    public void testWithoutParameters() throws Exception {
        createIndex(INDEX_1_NAME, INDEX_2_NAME);

        AcknowledgedResponse putMappingResponse = client().admin().indices().putMapping(
                new PutMappingRequest(INDEX_1_NAME)
                        .type(CUSTOM_TYPE)
                        .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"text\"}," +
                                "\"author\":{\"store\":true,\"type\":\"text\"}}}}", XContentType.JSON)
        ).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());


        assertConcreteMappingsOnAll(INDEX_1_NAME, CUSTOM_TYPE, "title", "author");

        RefreshResponse refreshResponse = client().admin().indices()
                .refresh(new RefreshRequest(INDEX_1_NAME, INDEX_2_NAME)).actionGet();
        assertEquals("All shards should have been refreshed", 0, refreshResponse.getFailedShards());

        GetMapping getMapping = new GetMapping.Builder().build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject resultJson = result.getJsonObject();
        assertNotNull("GetMapping response JSON should include the index " + INDEX_1_NAME,
                resultJson.getAsJsonObject(INDEX_1_NAME));
        assertNotNull("GetMapping response JSON should include the index " + INDEX_2_NAME,
                resultJson.getAsJsonObject(INDEX_2_NAME));
    }

    @Test
    public void testWithSingleIndex() throws Exception {
        createIndex(INDEX_1_NAME, INDEX_2_NAME);

        AcknowledgedResponse putMappingResponse = client().admin().indices().putMapping(
                new PutMappingRequest(INDEX_1_NAME)
                        .type(CUSTOM_TYPE)
                        .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"text\"}," +
                                "\"author\":{\"store\":true,\"type\":\"text\"}}}}", XContentType.JSON)
        ).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());
        assertConcreteMappingsOnAll(INDEX_1_NAME, CUSTOM_TYPE, "title", "author");

        RefreshResponse refreshResponse = client().admin().indices()
                .refresh(new RefreshRequest(INDEX_1_NAME, INDEX_2_NAME)).actionGet();
        assertEquals("All shards should have been refreshed", 0, refreshResponse.getFailedShards());

        Action getMapping = new GetMapping.Builder().addIndex(INDEX_2_NAME).build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        System.out.println("result.getJsonString() = " + result.getJsonString());
        JsonObject resultJson = result.getJsonObject();
        assertNotNull("GetMapping response JSON should include the index " + INDEX_2_NAME,
                resultJson.getAsJsonObject(INDEX_2_NAME));
    }

    @Test
    public void testWithMultipleIndices() throws Exception {
        createIndex(INDEX_1_NAME, INDEX_2_NAME, "irrelevant");

        AcknowledgedResponse putMappingResponse = client().admin().indices().putMapping(
                new PutMappingRequest(INDEX_1_NAME)
                        .type(CUSTOM_TYPE)
                        .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"text\"}," +
                                "\"author\":{\"store\":true,\"type\":\"text\"}}}}", XContentType.JSON)
        ).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());
        putMappingResponse = client().admin().indices().putMapping(
                new PutMappingRequest(INDEX_2_NAME)
                        .type(CUSTOM_TYPE)
                        .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":false,\"type\":\"text\"}," +
                                "\"isbn\":{\"store\":true,\"type\":\"text\"}}}}", XContentType.JSON)
        ).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());
        assertConcreteMappingsOnAll(INDEX_1_NAME, CUSTOM_TYPE, "title", "author");
        assertConcreteMappingsOnAll(INDEX_2_NAME, CUSTOM_TYPE, "title", "isbn");

        RefreshResponse refreshResponse = client().admin().indices()
                .refresh(new RefreshRequest(INDEX_1_NAME, INDEX_2_NAME)).actionGet();
        assertEquals("All shards should have been refreshed", 0, refreshResponse.getFailedShards());

        Action getMapping = new GetMapping.Builder().addIndex(INDEX_2_NAME).addIndex(INDEX_1_NAME).build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject resultJson = result.getJsonObject();
        assertNotNull("GetMapping response JSON should include the index " + INDEX_1_NAME,
                resultJson.getAsJsonObject(INDEX_1_NAME));
        assertNotNull("GetMapping response JSON should include the index " + INDEX_2_NAME,
                resultJson.getAsJsonObject(INDEX_2_NAME));
    }

}
