package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;
import org.apache.lucene.search.Explanation;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
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
    public void searchWithValidQuery() throws IOException {
        JestResult result = client.execute(new Search.Builder(query).build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void searchWithValidQueryAndExplain() throws IOException {
        client().index(new IndexRequest("twitter", "tweet").source("{\"user\":\"kimchy\"}").refresh(true)).actionGet();

        String queryWithExplain = "{\n" +
                "    \"explain\": true,\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }" +
                "}";

        JestResult result = client.execute(
                new Search.Builder(queryWithExplain).refresh(true).build()
        );
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
        assertEquals(1, hits.size());
        JsonElement explanation = hits.get(0).getAsJsonObject().get("_explanation");
        assertNotNull(explanation);
        logger.info("Explanation = {}", explanation);
    }

    @Test
    public void searchWithQueryBuilder() throws IOException {
        Index index = new Index.Builder("{\"user\":\"kimchy\"}").setParameter(Parameters.REFRESH, true).build();
        client.execute(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("user", "kimchy"));

        JestResult result = client.execute(new Search.Builder(searchSourceBuilder.toString()).build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void searchWithValidTermQuery() throws IOException {
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

        Search search = new Search.Builder(query)
                .addIndex("twitter")
                .addType("tweet")
                .setParameter(Parameters.SIZE, 1)
                .build();

        SearchResult result = client.execute(search);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        List<Object> resultList = result.getSourceAsObjectList(Object.class);
        assertEquals(1, resultList.size());
    }

    @Test
    public void searchAndGetFirstHit() throws IOException {
        client().index(
                new IndexRequest("articles", "article").source(new Gson().toJson(new TestArticleModel("pickles"))).refresh(true)
        ).actionGet();

        SearchResult searchResult = client.execute(new Search.Builder("{\n" +
                "    \"explain\": true,\n" +
                "    \"query\":{\n" +
                "        \"query_string\":{\n" +
                "            \"query\":\"pickles\"\n" +
                "        }\n" +
                "    },\n" +
                "   \"highlight\" : {\n" +
                "        \"fields\" : {\n" +
                "            \"name\" : {}\n" +
                "        }\n" +
                "    }" +
                "}").build());
        assertNotNull(searchResult);

        SearchResult.Hit<TestArticleModel, Explanation> hit = searchResult.getFirstHit(TestArticleModel.class, Explanation.class);
        assertNotNull(hit.source);
        assertNotNull(hit.explanation);
        assertNotNull(hit.highlight);
        assertEquals(1, hit.highlight.size());
        assertTrue(hit.highlight.containsKey("name"));
        assertEquals(1, hit.highlight.get("name").size());
    }

    @Test
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
