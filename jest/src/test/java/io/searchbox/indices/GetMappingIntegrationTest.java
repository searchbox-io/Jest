package io.searchbox.indices;

import com.google.gson.JsonObject;
import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.GetMapping;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 1)
public class GetMappingIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_1_NAME = "book";
    private static final String INDEX_2_NAME = "video";

    @Test
    public void testWithoutParameters() throws IOException, InterruptedException {
        createIndex(INDEX_1_NAME, INDEX_2_NAME);

        client().admin().indices().putMapping(new PutMappingRequest(INDEX_1_NAME)
                .type("science-fiction")
                .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"string\"}," +
                        "\"author\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();
        ensureSearchable(INDEX_1_NAME, INDEX_2_NAME);

        GetMapping getMapping = new GetMapping.Builder().build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.isSucceeded());

        JsonObject resultJson;
        JsonObject index1Object;
        JsonObject index2Object;
        int retries = 0;
        do {
            resultJson = result.getJsonObject();
            index1Object = resultJson.getAsJsonObject(INDEX_1_NAME);
            index2Object = resultJson.getAsJsonObject(INDEX_2_NAME);
            if(retries > 0) Thread.sleep(1000);
            retries++;
        } while ((index1Object == null || index2Object == null) && retries < 3);

        assertNotNull("GetMapping response JSON should include the index " + INDEX_1_NAME, index1Object);
        assertNotNull("GetMapping response JSON should include the index " + INDEX_2_NAME, index2Object);

        JsonObject index1Mappings = index2Object.getAsJsonObject("mappings");
        assertEquals(1, index1Mappings.entrySet().size());
        assertNotNull(index1Mappings.get("_default_"));

        JsonObject index2Mappings = index1Object.getAsJsonObject("mappings");
        assertEquals(2, index2Mappings.entrySet().size());
        assertNotNull(index2Mappings.get("_default_"));
        assertNotNull(index2Mappings.get("science-fiction"));
    }

    @Test
    public void testWithSingleIndex() throws IOException, InterruptedException {
        createIndex(INDEX_1_NAME, INDEX_2_NAME);

        client().admin().indices().putMapping(new PutMappingRequest(INDEX_1_NAME)
                .type("science-fiction")
                .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"string\"}," +
                        "\"author\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();
        ensureSearchable(INDEX_1_NAME, INDEX_2_NAME);

        Action getMapping = new GetMapping.Builder().addIndex(INDEX_2_NAME).build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.isSucceeded());

        JsonObject resultJson;
        JsonObject index2Object;
        int retries = 0;
        do {
            resultJson = result.getJsonObject();
            index2Object = resultJson.getAsJsonObject(INDEX_2_NAME);
            if(retries > 0) Thread.sleep(1000);
            retries++;
        } while (index2Object == null && retries < 3);
        assertNotNull("GetMapping response JSON should include the index " + INDEX_2_NAME, index2Object);

        JsonObject mappings = index2Object.getAsJsonObject("mappings");
        assertEquals(1, mappings.entrySet().size());
        assertNotNull(mappings.get("_default_"));
    }

    @Test
    public void testWithMultipleIndices() throws IOException, InterruptedException {
        createIndex(INDEX_1_NAME, INDEX_2_NAME, "irrelevant");

        client().admin().indices().putMapping(new PutMappingRequest(INDEX_1_NAME)
                .type("science-fiction")
                .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"string\"}," +
                        "\"author\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();
        ensureSearchable(INDEX_1_NAME, INDEX_2_NAME, "irrelevant");

        Action getMapping = new GetMapping.Builder().addIndex(INDEX_2_NAME).addIndex(INDEX_1_NAME).build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.isSucceeded());

        JsonObject resultJson;
        JsonObject index1Object;
        JsonObject index2Object;
        int retries = 0;
        do {
            resultJson = result.getJsonObject();
            index1Object = resultJson.getAsJsonObject(INDEX_1_NAME);
            index2Object = resultJson.getAsJsonObject(INDEX_2_NAME);
            if(retries > 0) Thread.sleep(1000);
            retries++;
        } while ((index1Object == null || index2Object == null) && retries < 3);
        assertNotNull("GetMapping response JSON should include the index " + INDEX_1_NAME, index1Object);
        assertNotNull("GetMapping response JSON should include the index " + INDEX_2_NAME, index2Object);

        JsonObject index2Mappings = index2Object.getAsJsonObject("mappings");
        assertEquals(1, index2Mappings.entrySet().size());
        assertNotNull(index2Mappings.get("_default_"));

        JsonObject index1Mappings = index1Object.getAsJsonObject("mappings");
        assertEquals(2, index1Mappings.entrySet().size());
        assertNotNull(index1Mappings.get("_default_"));
        assertNotNull(index1Mappings.get("science-fiction"));
    }

    /**
     * An interesting edge-case (?) test...
     * elasticsearch returns mapping of only the first index even if you specify "_all" as index name.
     *
     * @throws IOException
     * @see <a href="http://elasticsearch-users.115913.n3.nabble.com/TypeMissingException-type-all-missing-td3638313.html"></a>
     *      <p/>
     *      But the mapping api docs kinda contradicts with said behaviour...
     * @see <a href="http://www.elasticsearch.org/guide/reference/api/admin-indices-get-mapping/"></a>
     */
    @Ignore
    @Test
    public void testWithMultipleTypes() throws IOException {
        Action getMapping = new GetMapping.Builder().addType("science-fiction").build();
        JestResult result = client.execute(getMapping);
        assertNotNull(result);
        JsonObject resultJsonObject = result.getJsonObject();
        assertTrue("Get-mapping result should contain mapping for the added index name(s).",
                resultJsonObject.has(INDEX_1_NAME));
        assertTrue("Get-mapping result should contain mapping for the added index name(s).",
                resultJsonObject.has(INDEX_2_NAME));
    }

}
