package io.searchbox.core;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesAction;
import org.elasticsearch.test.ESIntegTestCase;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.JsonArray;

import io.searchbox.common.AbstractIntegrationTest;

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
}
