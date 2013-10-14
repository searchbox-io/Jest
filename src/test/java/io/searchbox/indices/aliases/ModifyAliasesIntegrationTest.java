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
    @ElasticsearchAdminClient
    AdminClient adminClient;

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = "my_index_0"),
            @ElasticsearchIndex(indexName = "my_index_1")
    })
    public void testAddAlias() throws IOException {
        String alias = "myAlias000";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder("my_index_0", alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState = adminClient.cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        Map<String, Map<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        Map<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(1, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get("my_index_0"));
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = "my_index_2"),
            @ElasticsearchIndex(indexName = "my_index_3")
    })
    public void testAddAliasForMultipleIndex() throws IOException {
        String alias = "myAlias001";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder("my_index_2", alias).addIndex("my_index_3").build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState = adminClient.cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        Map<String, Map<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        Map<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(2, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get("my_index_2"));
        assertNotNull(aliasMetaDataMap.get("my_index_3"));
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = "my_index_4"),
            @ElasticsearchIndex(indexName = "my_index_5")
    })
    public void testAddAliasWithRouting() throws IOException {
        String alias = "myAlias002";
        String routing = "3";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder("my_index_4", alias).addSearchRouting(routing).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState = adminClient.cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        Map<String, Map<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        Map<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(1, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get("my_index_4"));
        assertEquals(routing, aliasMetaDataMap.get("my_index_4").getSearchRouting());
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = "my_index_6"),
            @ElasticsearchIndex(indexName = "my_index_7")
    })
    public void testRemoveAlias() throws IOException {
        String alias = "myAlias003";

        IndicesAliasesResponse indicesAliasesResponse =
                adminClient.indices().aliases(new IndicesAliasesRequest().addAlias("my_index_6", alias)).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new RemoveAliasMapping.Builder("my_index_6", alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState = adminClient.cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        Map<String, Map<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        assertFalse(aliases.containsKey(alias));
    }

    @Test
    @ElasticsearchIndexes(indexes = {
            @ElasticsearchIndex(indexName = "my_index_8"),
            @ElasticsearchIndex(indexName = "my_index_9")
    })
    public void testAddAndRemoveAlias() throws IOException {
        String alias = "myAlias004";

        IndicesAliasesResponse indicesAliasesResponse =
                adminClient.indices().aliases(new IndicesAliasesRequest().addAlias("my_index_8", alias)).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new RemoveAliasMapping.Builder("my_index_8", alias).build()
        ).addAlias(
                new AddAliasMapping.Builder("my_index_9", alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState = adminClient.cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        Map<String, Map<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        Map<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(1, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get("my_index_9"));
    }

}
