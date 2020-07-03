package io.searchbox.core;

import com.google.gson.JsonArray;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.params.Parameters;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.test.ESIntegTestCase;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertAcked;


/**
 * @author ferhat
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 2)
public class SearchScrollIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX = "scroll_index";
    private static final String TYPE = "user";

    @Test
    public void searchWithValidQuery() throws IOException, JSONException {
        assertAcked(prepareCreate(INDEX).addMapping(TYPE, "{\"properties\":{\"code\":{\"type\":\"keyword\"}}}", XContentType.JSON));
        assertTrue(index(INDEX, TYPE, "swvq1", "{\"code\":\"0\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(index(INDEX, TYPE, "swvq2", "{\"code\":\"1\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(index(INDEX, TYPE, "swvq3", "{\"code\":\"2\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable(INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(1);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(INDEX)
                .addType(TYPE)
                .addSort(new Sort("code"))
                .setParameter(Parameters.SCROLL, "5m")
                .build();
        SearchResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
        assertEquals(
                "only 1 document should be returned",
                1,
                hits.size()
        );

        String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
        for (int i = 1; i < 3; i++) {
            SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m").build();
            result = client.execute(scroll);
            assertTrue(result.getErrorMessage(), result.isSucceeded());
            JSONAssert.assertEquals("{\"code\":\"" + i + "\"}", result.getSourceAsString(), false);
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
        JestResult clearResult = client.execute(clearScroll);
        assertTrue(clearResult.getErrorMessage(), clearResult.isSucceeded());
    }

    @Test
    public void clearScrollAll() throws IOException, JSONException {
        assertAcked(prepareCreate(INDEX).addMapping(TYPE, "{\"properties\":{\"code\":{\"type\":\"keyword\"}}}", XContentType.JSON));
        assertTrue(index(INDEX, TYPE, "swvq1", "{\"code\":\"0\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(index(INDEX, TYPE, "swvq2", "{\"code\":\"1\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(index(INDEX, TYPE, "swvq3", "{\"code\":\"2\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable(INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(1);

        Search search = new Search.Builder(searchSourceBuilder.toString())
            .addIndex(INDEX)
            .addType(TYPE)
            .addSort(new Sort("code"))
            .setParameter(Parameters.SCROLL, "5m")
            .build();
        SearchResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
        assertEquals(
            "only 1 document should be returned",
            1,
            hits.size()
        );

        String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
        for (int i = 1; i < 3; i++) {
            SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m").build();
            result = client.execute(scroll);
            assertTrue(result.getErrorMessage(), result.isSucceeded());
            JSONAssert.assertEquals("{\"code\":\"" + i + "\"}", result.getSourceAsString(), false);
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
        JestResult clearResult = client.execute(clearScroll);
        assertTrue(clearResult.getErrorMessage(), clearResult.isSucceeded());
    }

}
