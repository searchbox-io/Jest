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
            JestResult result = client.execute(new Search.Builder(query).build());
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
            Index index = new Index.Builder("{\"user\":\"kimchy\"}").setParameter(Parameters.REFRESH, true).build();
            client.execute(index);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery("user", "kimchy"));

            JestResult result = client.execute(new Search.Builder(searchSourceBuilder.toString()).build());
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

            Index index = new Index.Builder("{\"user\":\"kimchy\", \"content\":\"That is test\"}")
                    .index("twitter")
                    .type("tweet")
                    .setParameter(Parameters.REFRESH, true)
                    .build();
            client.execute(index);

            Index index2 = new Index.Builder("{\"user\":\"kimchy\", \"content\":\"That is test\"}")
                    .index("twitter")
                    .type("tweet")
                    .setParameter(Parameters.REFRESH, true)
                    .build();
            client.execute(index2);

            Search search = (Search) new Search.Builder(query)
                    .addIndex("twitter")
                    .addType("tweet")
                    .setParameter(Parameters.SIZE, 1)
                    .build();

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
        Index index = new Index.Builder(article)
                .index("articles")
                .type("article")
                .refresh(true)
                .build();
        client.execute(index);

        Search search = new Search.Builder("{\n" +
                "    \"query\":{\n" +
                "        \"query_string\":{\n" +
                "            \"query\":\"Jest\"\n" +
                "        }\n" +
                "    }\n" +
                "}")
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .addIndex("articles")
                .addType("article")
                .build();
        JestResult result = client.execute(search);
        List<TestArticleModel> articleResult = result.getSourceAsObjectList(TestArticleModel.class);
        assertNotNull(articleResult.get(0).getId());
    }
}
