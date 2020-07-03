package io.searchbox.indices.aliases;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class ModifyAliasesIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testAddAlias() throws IOException {
        String index0 = "my_index_0";
        String index1 = "my_index_1";
        createIndex((index0), index1);

        String alias = "myAlias000";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder((index0), alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        ClusterState clusterState =
                client().admin().cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        assertTrue(clusterState.getMetaData().hasAliases(new String[]{alias}, new String[]{index0}));
        assertFalse(clusterState.getMetaData().hasAliases(new String[]{alias}, new String[]{index1}));
    }

    @Test
    public void testAddAliasForMultipleIndex() throws IOException {
        String index2 = "my_index_2";
        String index3 = "my_index_3";
        createIndex(index2, index3);

        String alias = "myAlias001";

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new AddAliasMapping.Builder(index2, alias).addIndex(index3).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        ClusterState clusterState =
                client().admin().cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        assertTrue(clusterState.getMetaData().hasAliases(new String[]{alias}, new String[]{index2, index3}));
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
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        ClusterState clusterState =
                client().admin().cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        assertTrue(clusterState.getMetaData().hasAliases(new String[]{alias}, new String[]{"my_index_4"}));
        assertFalse(clusterState.getMetaData().hasAliases(new String[]{alias}, new String[]{"my_index_5"}));
        clusterState.getMetaData().findAliases(new String[]{alias}, new String[]{"my_index_4"}).get("my_index_4");
        List<AliasMetaData> indexMetadata = clusterState.getMetaData().findAliases(new String[]{alias}, new String[]{"my_index_4"}).get("my_index_4");
        assertEquals(1, indexMetadata.size());
        assertEquals(routing, indexMetadata.get(0).getSearchRouting());
    }

    @Test
    public void testRemoveAlias() throws IOException {
        createIndex("my_index_6", "my_index_7");

        String alias = "myAlias003";

        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions action = IndicesAliasesRequest.AliasActions.add().alias(alias).index("my_index_6");
        indicesAliasesRequest.addAliasAction(action);
        AcknowledgedResponse indicesAliasesResponse =
                client().admin().indices().aliases(indicesAliasesRequest).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new RemoveAliasMapping.Builder("my_index_6", alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        ClusterState clusterState =
                client().admin().cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        assertFalse(clusterState.getMetaData().hasAlias(alias));
    }

    @Test
    public void testAddAndRemoveAlias() throws IOException {
        createIndex("my_index_8", "my_index_9");
        String alias = "myAlias004";

        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions action = IndicesAliasesRequest.AliasActions.add().index("my_index_8").alias(alias);
        indicesAliasesRequest.addAliasAction(action);
        AcknowledgedResponse indicesAliasesResponse =
                client().admin().indices().aliases(indicesAliasesRequest).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());

        ModifyAliases modifyAliases = new ModifyAliases.Builder(
                new RemoveAliasMapping.Builder("my_index_8", alias).build()
        ).addAlias(
                new AddAliasMapping.Builder("my_index_9", alias).build()
        ).build();
        JestResult result = client.execute(modifyAliases);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        ClusterState clusterState =
                client().admin().cluster().state(new ClusterStateRequest()).actionGet(10, TimeUnit.SECONDS).getState();
        assertNotNull(clusterState);
        assertTrue(clusterState.getMetaData().hasAliases(new String[]{alias}, new String[]{"my_index_9"}));
        assertFalse(clusterState.getMetaData().hasAliases(new String[]{alias}, new String[]{"my_index_8"}));
    }

}
