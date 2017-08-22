package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;
import org.apache.lucene.search.Explanation;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertAcked;

/**
 * @author Dogukan Sonmez
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class SearchIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX = "twitter";
    private static final String TYPE = "tweet";

    String query = "{\n" +
            "  \"query\": {\n" +
            "    \"bool\": {\n" +
            "      \"must\": {\n" +
            "        \"match\": {\n" +
            "          \"content\": \"test\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"term\": {\n" +
            "          \"user\" : \"kimchy\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Test
    public void searchWithValidQuery() throws IOException {
        JestResult result = client.execute(new Search.Builder(query).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void searchWithMultipleHits() throws Exception {
        assertAcked(prepareCreate(INDEX).addMapping(TYPE, "{\"properties\":{\"user\":{\"type\":\"keyword\"}}}"));
        assertTrue(index(INDEX, TYPE, "swmh1", "{\"user\":\"kimchy1\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(index(INDEX, TYPE, "swmh2", "{\"user\":\"kimchy2\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(index(INDEX, TYPE, "swmh3", "{\"user\":\"kimchy3\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable(INDEX);

        SearchResult result = client.execute(new Search.Builder("").setParameter("sort", "user").build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<SearchResult.Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(3, hits.size());

        assertEquals(hits.get(0).id, "swmh1");
        assertEquals(hits.get(1).id, "swmh2");
        assertEquals(hits.get(2).id, "swmh3");

        JSONAssert.assertEquals("{\"user\":\"kimchy1\"}," +
                "{\"user\":\"kimchy2\"}," +
                "{\"user\":\"kimchy3\"}", result.getSourceAsString(), false);
    }

    @Test
    public void searchWithSourceFilterByQuery() throws Exception {
        assertAcked(prepareCreate(INDEX).addMapping(TYPE, "{\"properties\":{\"includeFieldName\":{\"type\":\"keyword\"}}}"));
        assertTrue(index(INDEX, TYPE, "Jeehong1", "{\"includeFieldName\":\"SeoHoo\",\"excludeFieldName\":\"SeongJeon\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(index(INDEX, TYPE, "Jeehong2",  "{\"includeFieldName\":\"Seola\",\"excludeFieldName\":\"SeongJeon\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable(INDEX);

        SearchResult result = client.execute(new Search.Builder("{\"sort\":\"includeFieldName\",\"_source\":{\"includes\":[\"includeFieldName\"]}}")
                                                     .addSourceExcludePattern("excludeFieldName").build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<SearchResult.Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(2,hits.size());
        assertEquals("{\"includeFieldName\":\"SeoHoo\"}," +
                     "{\"includeFieldName\":\"Seola\"}",result.getSourceAsString());
    }

    @Test
    public void searchWithSourceFilterByParam() throws Exception {
        assertAcked(prepareCreate(INDEX).addMapping(TYPE, "{\"properties\":{\"includeFieldName\":{\"type\":\"keyword\"}}}"));
        assertTrue(index(INDEX, TYPE, "Happyprg1", "{\"includeFieldName\":\"SeoHoo\",\"excludeFieldName\":\"SeongJeon\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(index(INDEX, TYPE, "Happyprg2",  "{\"includeFieldName\":\"Seola\",\"excludeFieldName\":\"SeongJeon\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable(INDEX);

        SearchResult result = client.execute(new Search.Builder("{\"sort\":\"includeFieldName\"}")
                                                     .addSourceIncludePattern("includeFieldName")
                                                     .addSourceExcludePattern("excludeFieldName").build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<SearchResult.Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(2,hits.size());
        assertEquals("{\"includeFieldName\":\"SeoHoo\"}," +
                     "{\"includeFieldName\":\"Seola\"}", result.getSourceAsString());
    }

    @Test
    public void searchWithSort() throws Exception {
        assertAcked(prepareCreate(INDEX).addMapping(TYPE, "{\"properties\":{\"user\":{\"type\":\"keyword\"}}}", XContentType.JSON));
        assertTrue(index(INDEX, TYPE, "sws1", "{\"user\":\"kimchy1\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(index(INDEX, TYPE, "sws2", "{\"user\":\"kimchy2\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable(INDEX);

        Search search = new Search.Builder("").setParameter("sort", "user").build();
        SearchResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<SearchResult.Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(1, hits.get(0).sort.size());
        assertEquals("kimchy1", hits.get(0).sort.get(0));
        assertEquals(null, hits.get(0).score);
        assertEquals(1, hits.get(1).sort.size());
        assertEquals("kimchy2", hits.get(1).sort.get(0));
        assertEquals(null, hits.get(1).score);

        search = new Search.Builder("").setParameter("sort", "user").enableTrackScores().build();
        result = client.execute(search);
        hits = result.getHits(Object.class);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1, hits.get(0).sort.size());
        assertEquals("kimchy1", hits.get(0).sort.get(0));
        assertEquals(new Double(1.0), hits.get(0).score);
        assertEquals(1, hits.get(1).sort.size());
        assertEquals("kimchy2", hits.get(1).sort.get(0));
        assertEquals(new Double(1.0), hits.get(1).score);
    }

    @Test
    public void searchWithValidQueryAndExplain() throws IOException {
        assertTrue(index(INDEX, TYPE, "swvqae1", "{\"user\":\"kimchy\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable(INDEX);

        String queryWithExplain = "{\n" +
                "    \"explain\": true,\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }" +
                "}";

        SearchResult result = client.execute(
                new Search.Builder(queryWithExplain).build()
        );
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
        assertEquals(1, hits.size());

        JsonElement explanation = hits.get(0).getAsJsonObject().get("_explanation");
        assertNotNull(explanation);
        assertEquals(new Long(1L), result.getTotal());
    }

    @Test
    public void searchWithQueryBuilder() throws IOException {
        assertTrue(index(INDEX, TYPE, "swqb1", "{\"user\":\"kimchy\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable(INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("user", "kimchy"));

        JestResult result = client.execute(new Search.Builder(searchSourceBuilder.toString()).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void searchWithValidTermQuery() throws IOException {
        assertTrue(index(INDEX, TYPE, "1", "{\"user\":\"kimchy\", \"content\":\"That is test\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(index(INDEX, TYPE, "2", "{\"user\":\"kimchy\", \"content\":\"That is test\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable(INDEX);

        Search search = new Search.Builder(query)
                .addIndex(INDEX)
                .addType(TYPE)
                .setParameter(Parameters.SIZE, 1)
                .build();

        SearchResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        List<Object> resultList = result.getSourceAsObjectList(Object.class);
        assertEquals(1, resultList.size());
    }

    @Test
    public void searchAndGetFirstHit() throws IOException {
        assertTrue(index("articles", "article", "3", new Gson().toJson(new TestArticleModel("pickles"))).getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable("articles");

        SearchResult searchResult = client.execute(new Search.Builder("{\n" +
                "    \"explain\": true,\n" +
                "    \"query\":{\n" +
                "        \"query_string\":{\n" +
                "            \"query\":\"name:pickles\"\n" +
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
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .addIndex("articles")
                .addType("article")
                .build();
        JestResult result = client.execute(search);
        List<TestArticleModel> articleResult = result.getSourceAsObjectList(TestArticleModel.class);
        assertNotNull(articleResult.get(0).getId());
    }
}
