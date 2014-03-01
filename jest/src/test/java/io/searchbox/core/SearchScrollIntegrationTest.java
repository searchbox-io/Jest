package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;


/**
 * @author ferhat
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 2)
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
                .setParameter(Parameters.SEARCH_TYPE, SearchType.SCAN)
                .setParameter(Parameters.SIZE, 1)
                .setParameter(Parameters.SCROLL, "5m")
                .build();
        JestResult result = client.execute(search);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
        for (Integer i = 1; i < 4; i++) {
            SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m")
                    .setParameter(Parameters.SIZE, 1).build();
            result = client.execute(scroll);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            assertEquals(
                    "only 1 document should be returned",
                    1,
                    result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits").size()
            );
            scrollId = result.getJsonObject().getAsJsonPrimitive("_scroll_id").getAsString();
        }

        SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m").build();
        result = client.execute(scroll);
        assertNotNull(result);
        assertEquals(
                "no results should be left to scroll at this point",
                0,
                result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits").size()
        );
    }

    private class User {
        private Integer code;

        private Integer getCode() {
            return code;
        }

        private void setCode(Integer code) {
            this.code = code;
        }
    }
}
