package io.searchbox.core;

import com.google.gson.JsonArray;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;


/**
 * @author ferhat
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 2)
public class SearchScrollIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "scroll_index";

    @Test
    public void searchWithValidQuery() throws IOException {
        client().index(new IndexRequest(INDEX_NAME, "user").source("{\"code\":\"1\"}").refresh(true)).actionGet();
        client().index(new IndexRequest(INDEX_NAME, "user").source("{\"code\":\"2\"}").refresh(true)).actionGet();
        client().index(new IndexRequest(INDEX_NAME, "user").source("{\"code\":\"3\"}").refresh(true)).actionGet();
        ensureSearchable(INDEX_NAME);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(INDEX_NAME)
                .addType("user")
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
        for (Integer i = 1; i < 3; i++) {
            SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m")
                    .setParameter(Parameters.SIZE, 1).build();
            result = client.execute(scroll);
            assertTrue(result.getErrorMessage(), result.isSucceeded());
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

        clearScroll(scrollId);
    }

}
