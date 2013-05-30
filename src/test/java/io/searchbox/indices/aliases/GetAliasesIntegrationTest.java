package io.searchbox.indices.aliases;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchAdminClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndexes;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.client.AdminClient;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;

/**
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class GetAliasesIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "aliases_test_index";
    private static final String INDEX_NAME_2 = "aliases_test_index2";
    private static final String INDEX_NAME_3 = "aliases_test_index3";
    @ElasticsearchAdminClient
    AdminClient adminClient;

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME),
            @ElasticsearchIndex(indexName = INDEX_NAME_2)
    })
    public void testGetAliases() throws IOException {
        String alias = "myAlias000";

        IndicesAliasesResponse indicesAliasesResponse =
                adminClient.indices().aliases(new IndicesAliasesRequest().addAlias(INDEX_NAME, alias)).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());

        GetAliases getAliases = new GetAliases.Builder().build();
        JestResult result = client.execute(getAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        assertEquals(1, result.getJsonObject().getAsJsonObject(INDEX_NAME).getAsJsonObject("aliases").entrySet().size());
        assertEquals(0, result.getJsonObject().getAsJsonObject(INDEX_NAME_2).getAsJsonObject("aliases").entrySet().size());
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME),
            @ElasticsearchIndex(indexName = INDEX_NAME_2)
    })
    public void testGetAliasesForSpecificIndex() throws IOException {
        String alias = "myAlias000";

        IndicesAliasesResponse indicesAliasesResponse =
                adminClient.indices().aliases(new IndicesAliasesRequest().addAlias(INDEX_NAME, alias)).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());

        GetAliases getAliases = new GetAliases.Builder().addIndex(INDEX_NAME).build();
        JestResult result = client.execute(getAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        assertEquals(1, result.getJsonObject().entrySet().size());
        assertEquals(1, result.getJsonObject().getAsJsonObject(INDEX_NAME).getAsJsonObject("aliases").entrySet().size());
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME + "_0"),
            @ElasticsearchIndex(indexName = INDEX_NAME_2 + "_0"),
            @ElasticsearchIndex(indexName = INDEX_NAME_3 + "_0")
    })
    public void testGetAliasesForMultipleSpecificIndices() throws IOException {
        String alias = "myAlias000";

        GetAliases getAliases = new GetAliases.Builder().addIndex(INDEX_NAME + "_0").addIndex(INDEX_NAME_3 + "_0").build();
        JestResult result = client.execute(getAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        assertEquals(2, result.getJsonObject().entrySet().size());
        assertEquals(0, result.getJsonObject().getAsJsonObject(INDEX_NAME + "_0").getAsJsonObject("aliases").entrySet().size());
        assertEquals(0, result.getJsonObject().getAsJsonObject(INDEX_NAME_3 + "_0").getAsJsonObject("aliases").entrySet().size());
    }

}
