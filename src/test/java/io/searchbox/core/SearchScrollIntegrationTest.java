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

            Index index = new Index.Builder("{\"code\":\"1\"}").index("scroll_index").type("user").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            index = new Index.Builder("{\"code\":\"2\"}").index("scroll_index").type("user").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            index = new Index.Builder("{\"code\":\"3\"}").index("scroll_index").type("user").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());

            Search search = new Search(searchSourceBuilder.toString());
            search.addIndex("scroll_index");
            search.addType("user");
            search.setSearchType(SearchType.SCAN);
            search.setSize(1);
            search.setScroll("5m");
            JestResult result = client.execute(search);
            assertNotNull(result);
            assertTrue(result.isSucceeded());

            String scrollId = result.getJsonObject().get("_scroll_id").getAsString();

            for (Integer i = 1; i < 4; i++) {
                SearchScroll scroll = new SearchScroll(scrollId, "5m");
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
