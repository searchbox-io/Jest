package io.searchbox.core;

import com.google.gson.JsonArray;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.params.Parameters;


/**
 * @author ferhat
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 2)
public class SearchScrollIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX = "scroll_index";
    private static final String TYPE = "user";

    @Test
    public void searchWithValidQuery() throws IOException {
        assertTrue(index(INDEX, TYPE, "swvq1", "{\"code\":\"0\"}").isCreated());
        assertTrue(index(INDEX, TYPE, "swvq2", "{\"code\":\"1\"}").isCreated());
        assertTrue(index(INDEX, TYPE, "swvq3", "{\"code\":\"2\"}").isCreated());
        refresh();
        ensureSearchable(INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(INDEX)
                .addType(TYPE)
                .addSort(new Sort("code"))
                .setParameter(Parameters.SIZE, 1)
                .setParameter(Parameters.SCROLL, "5m")
                .build();
        JestResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
        assertEquals(
                "only 1 document should be returned",
                1,
                hits.size()
        );

        String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
        for (int i = 1; i < 3; i++) {
            SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m")
                    .setParameter(Parameters.SIZE, 1).build();
            result = client.execute(scroll);
            assertTrue(result.getErrorMessage(), result.isSucceeded());
            assertEquals("{\"code\":\"" + i + "\"}", result.getSourceAsString());
            hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
            assertEquals(
                    "only 1 document should be returned",
                    1,
                    hits.size()
            );
            scrollId = result.getJsonObject().getAsJsonPrimitive("_scroll_id").getAsString();
        }

        SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m").build();
        result = client.execute(scroll);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(
                "no results should be left to scroll at this point",
                0,
                result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits").size()
        );

        // clear a single scroll id
        ClearScroll clearScroll = new ClearScroll.Builder().addScrollId(scrollId).build();
        result = client.execute(clearScroll);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void clearScrollAll() throws IOException {
        assertTrue(index(INDEX, TYPE, "swvq1", "{\"code\":\"0\"}").isCreated());
        assertTrue(index(INDEX, TYPE, "swvq2", "{\"code\":\"1\"}").isCreated());
        assertTrue(index(INDEX, TYPE, "swvq3", "{\"code\":\"2\"}").isCreated());
        refresh();
        ensureSearchable(INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        Search search = new Search.Builder(searchSourceBuilder.toString())
            .addIndex(INDEX)
            .addType(TYPE)
            .addSort(new Sort("code"))
            .setParameter(Parameters.SIZE, 1)
            .setParameter(Parameters.SCROLL, "5m")
            .build();
        JestResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
        assertEquals(
            "only 1 document should be returned",
            1,
            hits.size()
        );

        String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
        for (int i = 1; i < 3; i++) {
            SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m")
                .setParameter(Parameters.SIZE, 1).build();
            result = client.execute(scroll);
            assertTrue(result.getErrorMessage(), result.isSucceeded());
            assertEquals("{\"code\":\"" + i + "\"}", result.getSourceAsString());
            hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
            assertEquals(
                "only 1 document should be returned",
                1,
                hits.size()
            );
            scrollId = result.getJsonObject().getAsJsonPrimitive("_scroll_id").getAsString();
        }

        SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m").build();
        result = client.execute(scroll);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(
            "no results should be left to scroll at this point",
            0,
            result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits").size()
        );

        // clear all scroll ids
        ClearScroll clearScroll = new ClearScroll.Builder().build();
        result = client.execute(clearScroll);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

}
