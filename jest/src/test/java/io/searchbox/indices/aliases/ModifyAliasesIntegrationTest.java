package io.searchbox.indices.aliases;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.AliasAction;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class ModifyAliasesIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testAddAlias() throws IOException {
        createIndex("my_index_0", "my_index_1");

        String alias = "myAlias000";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder("my_index_0", alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState =
                client().admin().cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        ImmutableOpenMap<String,ImmutableOpenMap<String,AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        ImmutableOpenMap<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(1, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get("my_index_0"));
    }

    @Test
    public void testAddAliasForMultipleIndex() throws IOException {
        createIndex("my_index_2", "my_index_3");

        String alias = "myAlias001";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder("my_index_2", alias).addIndex("my_index_3").build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState =
                client().admin().cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        ImmutableOpenMap<String, ImmutableOpenMap<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        ImmutableOpenMap<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(2, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get("my_index_2"));
        assertNotNull(aliasMetaDataMap.get("my_index_3"));
    }

    @Test
    public void testAddAliasWithRouting() throws IOException {
        createIndex("my_index_4", "my_index_5");

        String alias = "myAlias002";
        String routing = "3";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder("my_index_4", alias).addSearchRouting(routing).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState =
                client().admin().cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        ImmutableOpenMap<String, ImmutableOpenMap<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        ImmutableOpenMap<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(1, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get("my_index_4"));
        assertEquals(routing, aliasMetaDataMap.get("my_index_4").getSearchRouting());
    }

    @Test
    public void testRemoveAlias() throws IOException {
        createIndex("my_index_6", "my_index_7");

        String alias = "myAlias003";

        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions action = new IndicesAliasesRequest.AliasActions(AliasAction.Type.ADD, "my_index_6", alias);
        indicesAliasesRequest.addAliasAction(action);
        IndicesAliasesResponse indicesAliasesResponse =
                client().admin().indices().aliases(indicesAliasesRequest).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new RemoveAliasMapping.Builder("my_index_6", alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        ClusterState clusterState =
                client().admin().cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        ImmutableOpenMap<String, ImmutableOpenMap<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        assertFalse(aliases.containsKey(alias));
    }

    @Test
    public void testAddAndRemoveAlias() throws IOException {
        createIndex("my_index_8", "my_index_9");
        String alias = "myAlias004";

        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions action = new IndicesAliasesRequest.AliasActions(AliasAction.Type.ADD, "my_index_8", alias);
        indicesAliasesRequest.addAliasAction(action);
        IndicesAliasesResponse indicesAliasesResponse =
                client().admin().indices().aliases(indicesAliasesRequest).actionGet(10, TimeUnit.SECONDS);
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

        ClusterState clusterState =
                client().admin().cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        ImmutableOpenMap<String, ImmutableOpenMap<String, AliasMetaData>> aliases = clusterState.getMetaData().getAliases();
        ImmutableOpenMap<String, AliasMetaData> aliasMetaDataMap = aliases.get(alias);
        assertNotNull(aliasMetaDataMap);
        assertEquals(1, aliasMetaDataMap.size());
        assertNotNull(aliasMetaDataMap.get("my_index_9"));
    }

}
