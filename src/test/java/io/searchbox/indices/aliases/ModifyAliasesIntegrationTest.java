package io.searchbox.indices.aliases;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchAdminClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndexes;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;

/**
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class ModifyAliasesIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "aliases_test_index";
    private static final String INDEX_NAME_2 = "aliases_test_index2";
    @ElasticsearchAdminClient
    AdminClient adminClient;

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME),
            @ElasticsearchIndex(indexName = INDEX_NAME_2)
    })
    public void testAddAlias() throws IOException {
        String alias = "myAlias000";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder(INDEX_NAME, alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState = adminClient.cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        ImmutableMap<String, ImmutableMap<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        assertEquals("There should be only one alias defined", 1, aliases.size());
        Map<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(1, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get(INDEX_NAME));
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME),
            @ElasticsearchIndex(indexName = INDEX_NAME_2)
    })
    public void testAddAliasForMultipleIndex() throws IOException {
        String alias = "myAlias000";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder(INDEX_NAME, alias).addIndex(INDEX_NAME_2).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState = adminClient.cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        ImmutableMap<String, ImmutableMap<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        assertEquals(1, aliases.size());
        Map<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(2, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get(INDEX_NAME));
        assertNotNull(aliasMetaDataMap.get(INDEX_NAME_2));
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME),
            @ElasticsearchIndex(indexName = INDEX_NAME_2)
    })
    public void testAddAliasWithRouting() throws IOException {
        String alias = "myAlias000";
        String routing = "3";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder(INDEX_NAME, alias).addSearchRouting(routing).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState = adminClient.cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        ImmutableMap<String, ImmutableMap<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        assertEquals("There should be only one alias defined", 1, aliases.size());
        Map<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(1, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get(INDEX_NAME));
        assertEquals(routing, aliasMetaDataMap.get(INDEX_NAME).getSearchRouting());
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME),
            @ElasticsearchIndex(indexName = INDEX_NAME_2)
    })
    public void testRemoveAlias() throws IOException {
        String alias = "myAlias000";

        IndicesAliasesResponse indicesAliasesResponse =
                adminClient.indices().aliases(new IndicesAliasesRequest().addAlias(INDEX_NAME, alias)).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new RemoveAliasMapping.Builder(INDEX_NAME, alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState = adminClient.cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        ImmutableMap<String, ImmutableMap<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        assertEquals(0, aliases.size());
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = INDEX_NAME),
            @ElasticsearchIndex(indexName = INDEX_NAME_2)
    })
    public void testAddAndRemoveAlias() throws IOException {
        String alias = "myAlias000";

        IndicesAliasesResponse indicesAliasesResponse =
                adminClient.indices().aliases(new IndicesAliasesRequest().addAlias(INDEX_NAME, alias)).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new RemoveAliasMapping.Builder(INDEX_NAME, alias).build()
        ).addAlias(
                new AddAliasMapping.Builder(INDEX_NAME_2, alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState = adminClient.cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        ImmutableMap<String, ImmutableMap<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        assertEquals(1, aliases.size());
        Map<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(1, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get(INDEX_NAME_2));
    }

}
