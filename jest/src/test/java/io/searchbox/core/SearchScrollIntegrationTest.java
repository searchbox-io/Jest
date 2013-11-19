package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


/**
 * @author ferhat
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class SearchScrollIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "scroll_index")
    public void searchWithValidQuery() {
        try {

            Index index = new Index.Builder("{\"code\":\"1\"}")
                    .index("scroll_index")
                    .type("user")
                    .setParameter(Parameters.REFRESH, true)
                    .build();
            client.execute(index);

            index = new Index.Builder("{\"code\":\"2\"}")
                    .index("scroll_index")
                    .type("user")
                    .setParameter(Parameters.REFRESH, true)
                    .build();
            client.execute(index);

            index = new Index.Builder("{\"code\":\"3\"}")
                    .index("scroll_index")
                    .type("user")
                    .setParameter(Parameters.REFRESH, true)
                    .build();
            client.execute(index);

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());

            Search search = new Search.Builder(searchSourceBuilder.toString())
                    .addIndex("scroll_index")
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
                SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m").build();
                result = client.execute(scroll);
                assertNotNull(result);
                assertTrue(result.isSucceeded());
                User user = result.getSourceAsObject(User.class);
                assertEquals(i, user.getCode());
            }
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
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
