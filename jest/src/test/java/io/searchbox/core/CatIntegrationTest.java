package io.searchbox.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import com.google.gson.JsonElement;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesAction;
import org.elasticsearch.test.ESIntegTestCase;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.JsonArray;

import io.searchbox.common.AbstractIntegrationTest;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Bartosz Polnik
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class CatIntegrationTest extends AbstractIntegrationTest {
    final static String INDEX = "catintegrationindex";
    final static String ALIAS = "catintegrationalias";
    final static String INDEX2 = "catintegrationindex2";

    @Test
    public void shouldReturnEmptyPlainTextForIndices() throws IOException {
        CatResult result = client.execute(new Cat.IndicesBuilder().build());
        assertEquals(new JsonArray(), result.getJsonObject().get(result.getPathToResult()));
        assertArrayEquals(new String[0][0], result.getPlainText());
    }

    @Test
    public void shouldProperlyMapSingleResult() throws IOException, JSONException {
        createIndex(INDEX);
        ensureSearchable(INDEX);

        CatResult result = client.execute(new Cat.IndicesBuilder().setParameter("h", "index,docs.count").build());
        assertArrayEquals(new String[][]{
                new String[]{"index", "docs.count"},
                new String[]{INDEX, "0"},
        }, result.getPlainText());
        JSONAssert.assertEquals("[{\"index\":\"catintegrationindex\",\"docs.count\":\"0\"}]", result.getSourceAsString(), false);
    }

    @Test
    public void shouldFilterResultsToASingleIndex() throws IOException, JSONException {
        createIndex(INDEX, INDEX2);
        ensureSearchable(INDEX, INDEX2);

        CatResult result = client.execute(new Cat.IndicesBuilder().setParameter("h", "index,docs.count").addIndex(INDEX2).build());
        assertArrayEquals(new String[][]{
                new String[]{"index", "docs.count"},
                new String[]{INDEX2, "0"},
        }, result.getPlainText());
        JSONAssert.assertEquals("[{\"index\":\"catintegrationindex2\",\"docs.count\":\"0\"}]", result.getSourceAsString(), false);
    }

    @Test
    public void shouldDisplayAliasForSingleResult() throws IOException, JSONException {
        createIndex(INDEX);
        ensureSearchable(INDEX);
        IndicesAliasesAction.INSTANCE.newRequestBuilder(client().admin().indices()).addAlias(INDEX, ALIAS).get();

        CatResult result = client.execute(new Cat.AliasesBuilder().setParameter("h", "alias,index").build());
        assertArrayEquals(new String[][]{
                new String[]{"alias", "index"},
                new String[]{ALIAS, INDEX},
        }, result.getPlainText());
        JSONAssert.assertEquals("[{\"alias\":\"catintegrationalias\",\"index\":\"catintegrationindex\"}]", result.getSourceAsString(), false);
    }

    @Test
    public void shouldDisplayRecoveryForSingleResult() throws IOException, JSONException {
        createIndex(INDEX);
        ensureSearchable(INDEX);

        CatResult result = client.execute(new Cat.RecoveryBuilder().addIndex(INDEX).setParameter("h", "index,stage").build());

        ArrayList<String[]> expectedPlainText = new ArrayList<>();
        ArrayList<String> recoveryResponsePerShared = new ArrayList<>();

        String expectedLine = "{\"index\":\"catintegrationindex\",\"stage\":\"done\"}";
        expectedPlainText.add(new String[]{"index", "stage"});

        IntStream.range(0, getNumShards(INDEX).totalNumShards).forEach(value -> {
            expectedPlainText.add(new String[]{INDEX, "done"});
            recoveryResponsePerShared.add(expectedLine);
        });
        assertArrayEquals(expectedPlainText.toArray(), result.getPlainText());

        String expectedSourceAsString = "[" + String.join(",", recoveryResponsePerShared) + "]";

        JSONAssert.assertEquals(expectedSourceAsString, result.getSourceAsString(), false);
    }

    @Test
    public void shouldChangeOrderOfColumnsByspecifyingParameters() throws IOException, JSONException {
        createIndex(INDEX);
        ensureSearchable(INDEX);
        IndicesAliasesAction.INSTANCE.newRequestBuilder(client().admin().indices()).addAlias(INDEX, ALIAS).get();

        CatResult result = client.execute(new Cat.AliasesBuilder().setParameter("h", "index,alias").build());
        assertArrayEquals(new String[][]{
                new String[]{"index", "alias"},
                new String[]{INDEX, ALIAS},
        }, result.getPlainText());
        JSONAssert.assertEquals("[{\"index\":\"catintegrationindex\",\"alias\":\"catintegrationalias\"}]", result.getSourceAsString(), false);
    }

    @Test
    public void catAllShards() throws IOException, JSONException {
        createIndex(INDEX);
        createIndex(INDEX2);
        ensureSearchable(INDEX);
        ensureSearchable(INDEX2);

        CatResult catResult = client.execute(new Cat.ShardsBuilder().setParameter("h", "index,docs").build());
        JsonArray shards = catResult.getJsonObject().get("result").getAsJsonArray();

        assertEquals(shards.size(), getNumShards(INDEX).totalNumShards + getNumShards(INDEX2).totalNumShards);

        int index1Count = 0;
        int index2Count = 0;
        for (JsonElement shard : shards) {
            index1Count += JSONCompare.compareJSON("{\"index\":\"" + INDEX + "\",\"docs\":\"0\"}", shard.toString(), JSONCompareMode.LENIENT).passed() ? 1 : 0;
            index2Count += JSONCompare.compareJSON("{\"index\":\"" + INDEX2 + "\",\"docs\":\"0\"}", shard.toString(), JSONCompareMode.LENIENT).passed() ? 1 : 0;
        }

        assertTrue(index1Count > 0);
        assertTrue(index2Count > 0);
        assertEquals(index1Count + index2Count, shards.size());
    }

    @Test
    public void catShardsSingleIndex() throws IOException, JSONException {
        createIndex(INDEX);
        createIndex(INDEX2);
        ensureSearchable(INDEX);
        ensureSearchable(INDEX2);

        CatResult catResult = client.execute(new Cat.ShardsBuilder().addIndex(INDEX).setParameter("h", "index,docs").build());
        JsonArray shards = catResult.getJsonObject().get("result").getAsJsonArray();

        assertEquals(shards.size(), getNumShards(INDEX).totalNumShards);

        for (JsonElement shard : shards) {
            JSONAssert.assertEquals("{\"index\":\"catintegrationindex\",\"docs\":\"0\"}", shard.toString(), false);
        }
    }

    @Test
    public void catNodes() throws IOException {
        CatResult catResult = client.execute(new Cat.NodesBuilder().setParameter("h", "name").build());
        JsonArray nodes = catResult.getJsonObject().get("result").getAsJsonArray();

        Set<String> expectedNodeNames = new HashSet<>(Arrays.asList(internalCluster().getNodeNames()));
        Set<String> actualNodeNames = new HashSet<>();
        for (JsonElement node : nodes) {
            actualNodeNames.add(node.getAsJsonObject().get("name").getAsString());
        }

        assertEquals(actualNodeNames, expectedNodeNames);
    }
}
