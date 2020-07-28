package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import io.searchbox.annotations.JestId;
import io.searchbox.annotations.JestVersion;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author cihat keser
 */
public class SearchResultTest {

    String json = "{\n" +
            "    \"_shards\":{\n" +
            "        \"total\" : 5,\n" +
            "        \"successful\" : 5,\n" +
            "        \"failed\" : 0\n" +
            "    },\n" +
            "    \"hits\":{\n" +
            "        \"total\" : 1,\n" +
            "        \"hits\" : [\n" +
            "            {\n" +
            "                \"_index\" : \"twitter\",\n" +
            "                \"_type\" : \"tweet\",\n" +
            "                \"_id\" : \"1\",\n" +
            "                \"_source\" : {\n" +
            "                    \"user\" : \"kimchy\",\n" +
            "                    \"postDate\" : \"2009-11-15T14:12:12\",\n" +
            "                    \"message\" : \"trying out Elasticsearch\"\n" +
            "                },\n" +
            "                \"sort\" : [\n" +
            "                     1234.5678\n" +
            "                ]\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    @Test
    public void testGetMaxScoreWhenMissing() {
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(json);
        searchResult.setJsonObject(new JsonParser().parse(json).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        Float maxScore = searchResult.getMaxScore();
        assertNull(maxScore);
    }

    @Test
    public void testGetMaxScoreNullMaxScore() {
        String jsonWithNullMaxScore = "{\n" +
                "    \"_shards\":{\n" +
                "        \"total\" : 5,\n" +
                "        \"successful\" : 5,\n" +
                "        \"failed\" : 0\n" +
                "    },\n" +
                "    \"hits\":{\n" +
                "        \"max_score\" : null,\n" +
                "        \"total\" : 1,\n" +
                "        \"hits\" : [\n" +
                "            {\n" +
                "                \"_index\" : \"twitter\",\n" +
                "                \"_type\" : \"tweet\",\n" +
                "                \"_id\" : \"1\",\n" +
                "                \"_source\" : {\n" +
                "                    \"user\" : \"kimchy\",\n" +
                "                    \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "                    \"message\" : \"trying out Elasticsearch\"\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(jsonWithNullMaxScore);
        searchResult.setJsonObject(new JsonParser().parse(jsonWithNullMaxScore).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        Float maxScore = searchResult.getMaxScore();
        assertNull(maxScore);
    }

    @Test
    public void testGetMaxScore() {
        String jsonWithMaxScore = "{\n" +
                "    \"_shards\":{\n" +
                "        \"total\" : 5,\n" +
                "        \"successful\" : 5,\n" +
                "        \"failed\" : 0\n" +
                "    },\n" +
                "    \"hits\":{\n" +
                "        \"max_score\" : 0.028130025,\n" +
                "        \"total\" : 1,\n" +
                "        \"hits\" : [\n" +
                "            {\n" +
                "                \"_index\" : \"twitter\",\n" +
                "                \"_type\" : \"tweet\",\n" +
                "                \"_id\" : \"1\",\n" +
                "                \"_source\" : {\n" +
                "                    \"user\" : \"kimchy\",\n" +
                "                    \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "                    \"message\" : \"trying out Elasticsearch\"\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(jsonWithMaxScore);
        searchResult.setJsonObject(new JsonParser().parse(jsonWithMaxScore).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        Float maxScore = searchResult.getMaxScore();
        assertNotNull(maxScore);
        assertEquals(new Float("0.028130025"), maxScore);
    }

    @Test
    public void testGetTotal() {
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(json);
        searchResult.setJsonObject(new JsonParser().parse(json).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        Long total = searchResult.getTotal();
        assertNotNull(total);
        assertEquals(new Long(1L), total);
    }

    @Test
    public void testGetTotalWhenTotalMissing() {
        String jsonWithoutTotal = "{\n" +
                "    \"_shards\":{\n" +
                "        \"total\" : 5,\n" +
                "        \"successful\" : 5,\n" +
                "        \"failed\" : 0\n" +
                "    },\n" +
                "    \"hits\":{\n" +
                "        \"hits\" : [\n" +
                "            {\n" +
                "                \"_index\" : \"twitter\",\n" +
                "                \"_type\" : \"tweet\",\n" +
                "                \"_id\" : \"1\",\n" +
                "                \"_source\" : {\n" +
                "                    \"user\" : \"kimchy\",\n" +
                "                    \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "                    \"message\" : \"trying out Elasticsearch\"\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(jsonWithoutTotal);
        searchResult.setJsonObject(new JsonParser().parse(jsonWithoutTotal).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        Long total = searchResult.getTotal();
        assertNull(total);
    }

    @Test
    public void testGetHits() {
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(json);
        searchResult.setJsonObject(new JsonParser().parse(json).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        List hits = searchResult.getHits(Object.class);
        assertNotNull(hits);
        assertFalse("should have 1 hit", hits.isEmpty());

        hits = searchResult.getHits(Object.class, Object.class);
        assertNotNull(hits);
        assertFalse("should have 1 hit", hits.isEmpty());
    }

    @Test
    public void testGetHitsWithoutMetadata() {
        final SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(json);
        searchResult.setJsonObject(new JsonParser().parse(json).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        assertTrue(getFirstHitSource(searchResult.getHits(Object.class)).containsKey(SearchResult.ES_METADATA_ID));
        assertFalse(getFirstHitSource(searchResult.getHits(Object.class, false)).containsKey(SearchResult.ES_METADATA_ID));
        assertTrue(getFirstHitSource(searchResult.getHits(Object.class, Object.class)).containsKey(SearchResult.ES_METADATA_ID));
        assertFalse(getFirstHitSource(searchResult.getHits(Object.class, Object.class, false)).containsKey(SearchResult.ES_METADATA_ID));
    }

    private Map getFirstHitSource(List hits) {
        assertNotNull(hits);
        assertTrue("should have 1 hit", hits.size() == 1);
        SearchResult.Hit hit = (SearchResult.Hit) hits.get(0);
        assertNotNull(hit.source);
        return (Map) hit.source;
    }

    @Test
    public void testGetFirstHit() {
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(json);
        searchResult.setJsonObject(new JsonParser().parse(json).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        SearchResult.Hit hit = searchResult.getFirstHit(Object.class);
        assertNotNull(hit);
        assertNotNull(hit.source);
        assertNull(hit.explanation);
        assertNotNull(hit.sort);
        assertNotNull(hit.id);
        assertNull(hit.score);
        assertEquals(Collections.emptyList(), hit.matchedQueries);

        hit = searchResult.getFirstHit(Object.class, Object.class);
        assertNotNull(hit);
        assertNotNull(hit.source);
        assertNull(hit.explanation);
        assertNotNull(hit.sort);
        assertNotNull(hit.id);
        assertNull(hit.score);
        assertEquals(Collections.emptyList(), hit.matchedQueries);
    }

    @Test
    public void testGetHitsWhenOperationFails() {
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(false);

        List hits = searchResult.getHits(Object.class);
        assertNotNull(hits);
        assertTrue(hits.isEmpty());

        hits = searchResult.getHits(Object.class, Object.class);
        assertNotNull(hits);
        assertTrue(hits.isEmpty());
    }

    @Test
    public void testGetFirstHitWhenOperationFails() {
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(false);

        SearchResult.Hit hit = searchResult.getFirstHit(Object.class);
        assertNull(hit);

        hit = searchResult.getFirstHit(Object.class, Object.class);
        assertNull(hit);
    }

    @Test
    public void testGetScore() {
        String jsonWithScore = "{\n" +
                "    \"_shards\":{\n" +
                "        \"total\" : 5,\n" +
                "        \"successful\" : 5,\n" +
                "        \"failed\" : 0\n" +
                "    },\n" +
                "    \"hits\":{\n" +
                "        \"total\" : 1,\n" +
                "        \"hits\" : [\n" +
                "            {\n" +
                "                \"_index\" : \"twitter\",\n" +
                "                \"_type\" : \"tweet\",\n" +
                "                \"_score\" : \"1.02332\",\n" +
                "                \"_id\" : \"1\",\n" +
                "                \"_source\" : {\n" +
                "                    \"user\" : \"kimchy\",\n" +
                "                    \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "                    \"message\" : \"trying out Elasticsearch\"\n" +
                "                },\n" +
                "                \"sort\" : [\n" +
                "                     1234.5678\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(jsonWithScore);
        searchResult.setJsonObject(new JsonParser().parse(jsonWithScore).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        SearchResult.Hit hit = searchResult.getFirstHit(Object.class);
        assertNotNull(hit);
        assertNotNull(hit.source);
        assertNull(hit.explanation);
        assertNotNull(hit.sort);
        assertNotNull(hit.id);
        assertNotNull(hit.score);

        hit = searchResult.getFirstHit(Object.class, Object.class);
        assertNotNull(hit);
        assertNotNull(hit.source);
        assertNull(hit.explanation);
        assertNotNull(hit.sort);
        assertNotNull(hit.id);
        assertNotNull(hit.score);
    }

    @Test
    public void testGetVersion() {
        Long someVersion = Integer.MAX_VALUE + 10L;

        String jsonWithVersion = "{\n" +
                "    \"_shards\":{\n" +
                "        \"total\" : 5,\n" +
                "        \"successful\" : 5,\n" +
                "        \"failed\" : 0\n" +
                "    },\n" +
                "    \"hits\":{\n" +
                "        \"total\" : 1,\n" +
                "        \"hits\" : [\n" +
                "            {\n" +
                "                \"_index\" : \"twitter\",\n" +
                "                \"_type\" : \"tweet\",\n" +
                "                \"_score\" : \"1.02332\",\n" +
                "                \"_id\" : \"1\",\n" +
                "                \"_version\" : \"" + someVersion + "\",\n" +
                "                \"_source\" : {\n" +
                "                    \"user\" : \"kimchy\",\n" +
                "                    \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "                    \"message\" : \"trying out Elasticsearch\"\n" +
                "                },\n" +
                "                \"sort\" : [\n" +
                "                     1234.5678\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(jsonWithVersion);
        searchResult.setJsonObject(new JsonParser().parse(jsonWithVersion).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        SearchResult.Hit<TestObject, Void> hit = searchResult.getFirstHit(TestObject.class);
        assertNotNull(hit.source);
        assertNull(hit.explanation);
        assertNotNull(hit.sort);
        assertNotNull(hit.score);
        assertEquals("Incorrect version", someVersion, hit.source.getVersion());
    }
    
    
    /*
     * This result is an "not real" example of a inner hit parent/child
     */
    @Test
    public void testParentChild() {
        Long someVersion = Integer.MAX_VALUE + 10L;

        String jsonWithVersion = "{\n" +
                "    \"_shards\":{\n" +
                "        \"total\" : 5,\n" +
                "        \"successful\" : 5,\n" +
                "        \"failed\" : 0\n" +
                "    },\n" +
                "    \"hits\":{\n" +
                "        \"total\" : 1,\n" +
                "        \"hits\" : [\n" +
                "            {\n" +
                "                \"_index\" : \"twitter\",\n" +
                "                \"_type\" : \"tweet\",\n" +
                "                \"_score\" : \"1.02332\",\n" +
                "				 \"_routing\": \"1\", \n" +
                "				 \"_parent\": \"1\", \n" +                
                "                \"_id\" : \"1\",\n" +
                "                \"_version\" : \"" + someVersion + "\",\n" +
                "                \"_source\" : {\n" +
                "                    \"user\" : \"kimchy\",\n" +
                "                    \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "                    \"message\" : \"trying out Elasticsearch\"\n" +
                "                },\n" +
                "                \"sort\" : [\n" +
                "                     1234.5678\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(jsonWithVersion);
        searchResult.setJsonObject(new JsonParser().parse(jsonWithVersion).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        SearchResult.Hit<TestObject, Void> hit = searchResult.getFirstHit(TestObject.class);
        assertNotNull(hit.source);
        assertNull(hit.explanation);
        assertNotNull(hit.sort);
        assertNotNull(hit.score);
        assertNotNull(hit.parent);
        assertNotNull(hit.routing);
        assertEquals("Incorrect version", someVersion, hit.source.getVersion());
    }  

    @Test
    public void testGetMatchedQueries() {
        String jsonWithScore = "{\n" +
                "    \"_shards\":{\n" +
                "        \"total\" : 5,\n" +
                "        \"successful\" : 5,\n" +
                "        \"failed\" : 0\n" +
                "    },\n" +
                "    \"hits\":{\n" +
                "        \"total\" : 1,\n" +
                "        \"hits\" : [\n" +
                "            {\n" +
                "                \"_index\" : \"twitter\",\n" +
                "                \"_type\" : \"tweet\",\n" +
                "                \"_score\" : \"1.02332\",\n" +
                "                \"_id\" : \"1\",\n" +
                "                \"_source\" : {\n" +
                "                    \"user\" : \"kimchy\",\n" +
                "                    \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "                    \"message\" : \"trying out Elasticsearch\"\n" +
                "                },\n" +
                "                \"sort\" : [\n" +
                "                     1234.5678\n" +
                "                ], \n" +
                "                \"matched_queries\" : [\"some-query\"] \n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(jsonWithScore);
        searchResult.setJsonObject(new JsonParser().parse(jsonWithScore).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");

        SearchResult.Hit hit = searchResult.getFirstHit(Object.class);
        assertNotNull(hit);
        assertNotNull(hit.source);
        assertNull(hit.explanation);
        assertNotNull(hit.sort);
        assertNotNull(hit.id);
        assertNotNull(hit.score);
        assertEquals("Incorrect Matched Query", Collections.singletonList("some-query"), hit.matchedQueries);

        hit = searchResult.getFirstHit(Object.class, Object.class);
        assertNotNull(hit);
        assertNotNull(hit.source);
        assertNull(hit.explanation);
        assertNotNull(hit.sort);
        assertNotNull(hit.id);
        assertNotNull(hit.score);
        assertEquals("Incorrect Matched Query", Collections.singletonList("some-query"), hit.matchedQueries);
    }

    @Test
    public void testGetHitsWithoutSource() {
        Long version = 2L;
        String jsonWithoutSource = "{\n" +
                "    \"_shards\":{\n" +
                "        \"total\" : 5,\n" +
                "        \"successful\" : 5,\n" +
                "        \"failed\" : 0\n" +
                "    },\n" +
                "    \"hits\":{\n" +
                "        \"total\" : 1,\n" +
                "        \"hits\" : [\n" +
                "            {\n" +
                "                \"_index\" : \"twitter\",\n" +
                "                \"_type\" : \"tweet\",\n" +
                "                \"_score\" : \"1.02332\",\n" +
                "                \"_id\" : \"1\",\n" +
                "                \"_version\" : \"" + version + "\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(jsonWithoutSource);
        searchResult.setJsonObject(new JsonParser().parse(jsonWithoutSource).getAsJsonObject());
        searchResult.setPathToResult("hits/hits/_source");
        SearchResult.Hit<TestObject, Void> hit = searchResult.getFirstHit(TestObject.class);
        assertNotNull(hit.source);
        assertNull(hit.explanation);
        assertNull(hit.sort);
        assertNotNull(hit.score);
        assertEquals("1", hit.source.getId());
        assertEquals(version, hit.source.getVersion());
    }

    class TestObject {
        @JestId
        private String id;

        @JestVersion
        private Long version;

        public TestObject() {}

        public Long getVersion() {
            return version;
        }

        public String getId() {
            return id;
        }
    }

}
