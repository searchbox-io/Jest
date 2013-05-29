package io.searchbox.indices.aliases;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchAdminClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class ModifyAliasesIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "aliases_test_index";
    @ElasticsearchAdminClient
    AdminClient adminClient;

    @Test
    @ElasticsearchIndex(indexName = INDEX_NAME)
    public void testAddAlias() throws IOException {
        String alias = "myAlias000";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder(INDEX_NAME, alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        IndicesAliasesResponse indicesAliasesResponse =
                adminClient.indices().aliases(new IndicesAliasesRequest()).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
    }

}
