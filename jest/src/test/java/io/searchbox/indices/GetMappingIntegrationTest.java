package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.*;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import com.google.gson.JsonObject;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.GetMapping;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class GetMappingIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_1_NAME = "book";
    private static final String INDEX_2_NAME = "video";

    private static final Set<String> INDEX_NAMES = new HashSet<String>(2);

    {
        INDEX_NAMES.add(INDEX_1_NAME);
        INDEX_NAMES.add(INDEX_2_NAME);
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_1_NAME, mappings = {
                    @ElasticsearchMapping(typeName = "science-fiction",
                            properties = {
                                    @ElasticsearchMappingField(name = "title", store = ElasticsearchMappingField.Store.Yes, type = ElasticsearchMappingField.Types.String),
                                    @ElasticsearchMappingField(name = "author", store = ElasticsearchMappingField.Store.Yes, type = ElasticsearchMappingField.Types.String)
                            })
            }),
            @ElasticsearchIndex(indexName = INDEX_2_NAME)}
    )
    public void testWithoutParameters() throws IOException {
        GetMapping getMapping = new GetMapping.Builder().build();
        JestResult result = client.execute(getMapping);
        assertNotNull(result);
        String jsonResult = result.getJsonString();
        for (String indexName : INDEX_NAMES) {
            assertTrue("Get-mapping result should contain results for all indices when called without parameters.",
                    jsonResult.contains(indexName));
        }
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_1_NAME, mappings = {
                    @ElasticsearchMapping(typeName = "science-fiction",
                            properties = {
                                    @ElasticsearchMappingField(name = "title", store = ElasticsearchMappingField.Store.Yes, type = ElasticsearchMappingField.Types.String),
                                    @ElasticsearchMappingField(name = "author", store = ElasticsearchMappingField.Store.Yes, type = ElasticsearchMappingField.Types.String)
                            })
            }),
            @ElasticsearchIndex(indexName = INDEX_2_NAME)}
    )
    public void testWithSingleIndex() throws IOException {
        Action getMapping = new GetMapping.Builder().addIndex(INDEX_2_NAME).build();
        JestResult result = client.execute(getMapping);
        assertNotNull(result);
        String jsonResult = result.getJsonString();
        assertTrue("Get-mapping result should contain mapping for the added index name(s).",
                jsonResult.contains(INDEX_2_NAME));
        assertFalse("Get-mapping result should not contain mapping for not added index name(s).",
                jsonResult.contains(INDEX_1_NAME));
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_1_NAME, mappings = {
                    @ElasticsearchMapping(typeName = "science-fiction",
                            properties = {
                                    @ElasticsearchMappingField(name = "title", store = ElasticsearchMappingField.Store.Yes, type = ElasticsearchMappingField.Types.String),
                                    @ElasticsearchMappingField(name = "author", store = ElasticsearchMappingField.Store.Yes, type = ElasticsearchMappingField.Types.String)
                            })
            }),
            @ElasticsearchIndex(indexName = INDEX_2_NAME),
            @ElasticsearchIndex(indexName = "irrelevant")}
    )
    public void testWithMultipleIndices() throws IOException {
        Action getMapping = new GetMapping.Builder().addIndex(INDEX_2_NAME).addIndex(INDEX_1_NAME).build();
        JestResult result = client.execute(getMapping);
        assertNotNull(result);
        JsonObject resultJsonObject = result.getJsonObject();
        assertTrue("Get-mapping result should contain mapping for the added index name(s).",
                resultJsonObject.has(INDEX_1_NAME));
        assertTrue("Get-mapping result should contain mapping for the added index name(s).",
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
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_1_NAME),
            @ElasticsearchIndex(indexName = INDEX_2_NAME, mappings = {
                    @ElasticsearchMapping(typeName = "science-fiction", compress = false,
                            properties = {
                                    @ElasticsearchMappingField(name = "title", store = ElasticsearchMappingField.Store.No, type = ElasticsearchMappingField.Types.String),
                                    @ElasticsearchMappingField(name = "author", store = ElasticsearchMappingField.Store.No, type = ElasticsearchMappingField.Types.String)
                            }
                    )
            }),
            @ElasticsearchIndex(indexName = "irrelevant", mappings = {
                    @ElasticsearchMapping(typeName = "science-fiction", compress = false,
                            properties = {
                                    @ElasticsearchMappingField(name = "ccc", store = ElasticsearchMappingField.Store.No, type = ElasticsearchMappingField.Types.String),
                                    @ElasticsearchMappingField(name = "cccccc", store = ElasticsearchMappingField.Store.No, type = ElasticsearchMappingField.Types.String)
                            }
                    )
            })}
    )
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
