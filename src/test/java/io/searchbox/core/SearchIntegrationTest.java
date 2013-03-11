package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class SearchIntegrationTest extends AbstractIntegrationTest {

    String query = "{\n" +
            "    \"query\": {\n" +
            "        \"filtered\" : {\n" +
            "            \"query\" : {\n" +
            "                \"query_string\" : {\n" +
            "                    \"query\" : \"test\"\n" +
            "                }\n" +
            "            },\n" +
            "            \"filter\" : {\n" +
            "                \"term\" : { \"user\" : \"kimchy\" }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";

    @Test
    @ElasticsearchIndex(indexName = "cvbank")
    public void searchWithValidQuery() {
        try {
            JestResult result = client.execute(new Search(query));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "cvbank")
    public void searchWithQueryBuilder() {
        try {
            Index index = new Index.Builder("{\"user\":\"kimchy\"}").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery("user", "kimchy"));

            JestResult result = client.execute(new Search(searchSourceBuilder.toString()));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void searchWithValidTermQuery() {
        try {

            Index index = new Index.Builder("{\"user\":\"kimchy\", \"content\":\"That is test\"}").index("twitter").type("tweet").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            Index index2 = new Index.Builder("{\"user\":\"kimchy\", \"content\":\"That is test\"}").index("twitter").type("tweet").build();
            index2.addParameter(Parameters.REFRESH, true);
            client.execute(index2);

            Search search = new Search(query);
            search.addIndex("twitter");
            search.addType("tweet");
            search.addParameter(Parameters.SIZE, 1);

            JestResult result = client.execute(search);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            List<Object> resultList = result.getSourceAsObjectList(Object.class);
            assertEquals(1, resultList.size());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "articles")
    public void searchIndexWithTypeWithNullJestId() throws Exception {
        TestArticleModel article = new TestArticleModel();
        article.setName("Jest");
        Index index = new Index.Builder(article).index("articles").type("article").build();
        index.addParameter(Parameters.REFRESH, true);
        client.execute(index);

        Search search = new Search("{\n" +
                "    \"query\":{\n" +
                "        \"query_string\":{\n" +
                "            \"query\":\"Jest\"\n" +
                "        }\n" +
                "    }\n" +
                "}");
        search.addIndex("articles");
        search.addType("article");
        search.setSearchType(SearchType.QUERY_AND_FETCH);
        JestResult result = client.execute(search);
        List<TestArticleModel> articleResult = result.getSourceAsObjectList(TestArticleModel.class);
        assertNotNull(articleResult.get(0).getId());
    }
}
