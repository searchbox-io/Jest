package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.*;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.GetMapping;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        for(String indexName : INDEX_NAMES) {
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
        Action getMapping = new GetMapping.Builder().addIndexName(INDEX_2_NAME).build();
        JestResult result = client.execute(getMapping);
        assertNotNull(result);
        String jsonResult = result.getJsonString();
        assertTrue("Get-mapping result should contain mapping for the added index name(s).",
                jsonResult.contains(INDEX_2_NAME));
        assertFalse("Get-mapping result should not contain mapping for not added index name(s).",
                jsonResult.contains(INDEX_1_NAME));
    }

}
