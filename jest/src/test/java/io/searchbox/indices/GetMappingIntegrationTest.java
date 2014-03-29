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
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numNodes = 1)
public class GetMappingIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_1_NAME = "book";
    private static final String INDEX_2_NAME = "video";

    @Test
    public void testWithoutParameters() throws IOException {
        createIndex(INDEX_1_NAME, INDEX_2_NAME);
        client().admin().indices().putMapping(new PutMappingRequest(INDEX_1_NAME)
                .type("science-fiction")
                .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"string\"}," +
                        "\"author\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();

        GetMapping getMapping = new GetMapping.Builder().build();
        JestResult result = client.execute(getMapping);
        assertNotNull(result);
        String jsonResult = result.getJsonString();
        assertTrue("Get-mapping result should contain results for all indices when called without parameters.",
                jsonResult.contains(INDEX_1_NAME));
        assertFalse("Get-mapping result should not contain results for not customized index.",
                jsonResult.contains(INDEX_2_NAME));
    }

    @Test
    public void testWithSingleIndex() throws IOException {
        createIndex(INDEX_1_NAME, INDEX_2_NAME);
        client().admin().indices().putMapping(new PutMappingRequest(INDEX_1_NAME)
                .type("science-fiction")
                .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"string\"}," +
                        "\"author\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();

        Action getMapping = new GetMapping.Builder().addIndex(INDEX_2_NAME).build();
        JestResult result = client.execute(getMapping);
        assertNotNull(result);
        String jsonResult = result.getJsonString();
        assertFalse("Get-mapping result should not contain mapping for the unconfigured index.",
                jsonResult.contains(INDEX_2_NAME));
        assertFalse("Get-mapping result should not contain mapping for not added index name(s).",
                jsonResult.contains(INDEX_1_NAME));
    }

    @Test
    public void testWithMultipleIndices() throws IOException {
        createIndex(INDEX_1_NAME, INDEX_2_NAME, "irrelevant");
        client().admin().indices().putMapping(new PutMappingRequest(INDEX_1_NAME)
                .type("science-fiction")
                .source("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"string\"}," +
                        "\"author\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();

        Action getMapping = new GetMapping.Builder().addIndex(INDEX_2_NAME).addIndex(INDEX_1_NAME).build();
        JestResult result = client.execute(getMapping);
        assertNotNull(result);
        JsonObject resultJsonObject = result.getJsonObject();
        assertTrue("Get-mapping result should contain mapping for the added index name(s).",
                resultJsonObject.has(INDEX_1_NAME));
        assertFalse("Get-mapping result should not contain mapping for the unconfigured index",
                resultJsonObject.has(INDEX_2_NAME));
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
