package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.util.List;

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
            "                }\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    @Test
    public void testGetHits() {
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(json);
        searchResult.setJsonObject(new JsonParser().parse(json).getAsJsonObject());

        List hits = searchResult.getHits(Object.class);
        assertNotNull(hits);
        assertFalse("should have 1 hit", hits.isEmpty());

        hits = searchResult.getHits(Object.class, Object.class);
        assertNotNull(hits);
        assertFalse("should have 1 hit", hits.isEmpty());
    }

    @Test
    public void testGetFirstHit() {
        SearchResult searchResult = new SearchResult(new Gson());
        searchResult.setSucceeded(true);
        searchResult.setJsonString(json);
        searchResult.setJsonObject(new JsonParser().parse(json).getAsJsonObject());

        SearchResult.Hit hit = searchResult.getFirstHit(Object.class);
        assertNotNull(hit);
        assertNotNull(hit.source);
        assertNull(hit.explanation);

        hit = searchResult.getFirstHit(Object.class, Object.class);
        assertNotNull(hit);
        assertNotNull(hit.source);
        assertNull(hit.explanation);
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

}
