package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Dogukan Sonmez
 */
public class MultiSearchTest {

    @Test
    public void singleMultiSearchWithoutIndex() throws JSONException {
        String expectedData = " {\"index\" : \"_all\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).build();

        assertEquals("POST", multiSearch.getRestMethodName());
        assertEquals("/_msearch", multiSearch.getURI());
        JSONAssert.assertEquals(expectedData, multiSearch.getData(null).toString(), false);
    }

    @Test
    public void singleMultiSearchWitIndex() throws JSONException {
        String expectedData = " {\"index\" : \"twitter\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}")
                .addIndex("twitter")
                .build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).build();

        assertEquals("POST", multiSearch.getRestMethodName());
        assertEquals("/_msearch", multiSearch.getURI());
        JSONAssert.assertEquals(expectedData, multiSearch.getData(null).toString(), false);
    }

    @Test
    public void multiSearchWitIndex() throws JSONException {
        String expectedData = " {\"index\" : \"twitter\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n" +
                "{\"index\" : \"_all\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}")
                .addIndex("twitter")
                .build();
        Search search2 = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).addSearch(search2).build();

        assertEquals("POST", multiSearch.getRestMethodName());
        assertEquals("/_msearch", multiSearch.getURI());
        JSONAssert.assertEquals(expectedData, multiSearch.getData(null).toString(), false);
    }

    @Test
    public void equals() {
        Search search1 = new Search.Builder("{\"match_all\" : {}}")
                .addIndex("twitter")
                .build();
        Search search2 = new Search.Builder("{\"match_all\" : {}}").build();

        MultiSearch multiSearch1 = new MultiSearch.Builder(Arrays.asList(search1, search2)).build();
        MultiSearch multiSearch1Duplicate = new MultiSearch.Builder(Arrays.asList(search1, search2)).build();

        assertEquals(multiSearch1, multiSearch1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSearches() {
        Search search1 = new Search.Builder("{\"match_all\" : {}}")
                .addIndex("twitter")
                .build();
        Search search2 = new Search.Builder("{\"match_all\" : {}}").build();

        MultiSearch multiSearch1 = new MultiSearch.Builder(search1).build();
        MultiSearch multiSearch1Duplicate = new MultiSearch.Builder(search2).build();

        assertNotEquals(multiSearch1, multiSearch1Duplicate);
    }

    @Test
    public void multiSearchResponse() {
        String json = "{\n" +
                "   \"responses\": [\n" +
                "      {\n" +
                "          \"_shards\":{\n" +
                "              \"total\" : 5,\n" +
                "              \"successful\" : 5,\n" +
                "              \"failed\" : 0\n" +
                "          },\n" +
                "          \"hits\":{\n" +
                "              \"total\" : 1,\n" +
                "              \"hits\" : [\n" +
                "                  {\n" +
                "                      \"_index\" : \"twitter\",\n" +
                "                      \"_type\" : \"tweet\",\n" +
                "                      \"_id\" : \"1\",\n" +
                "                      \"_source\" : {\n" +
                "                          \"user\" : \"kimchy\",\n" +
                "                          \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "                          \"message\" : \"trying out Elasticsearch\"\n" +
                "                      },\n" +
                "                      \"sort\" : [\n" +
                "                           1234.5678\n" +
                "                      ]\n" +
                "                  }\n" +
                "              ]\n" +
                "          }\n" +
                "      },\n" +
                "      {\n" +
                "          \"_shards\":{\n" +
                "              \"total\" : 5,\n" +
                "              \"successful\" : 5,\n" +
                "              \"failed\" : 0\n" +
                "          },\n" +
                "          \"hits\":{\n" +
                "              \"total\" : 0,\n" +
                "              \"hits\" : [ ]\n" +
                "          }\n" +
                "      },\n" +
                "      {\n" +
                "         \"error\": \"There was a \\\"test\\\" error\"\n" +
                "      }\n" +
                "   ]\n" +
                "}";

        MultiSearchResult multiSearchResult = new MultiSearchResult(new Gson());
        multiSearchResult.setSucceeded(true);
        multiSearchResult.setJsonString(json);
        multiSearchResult.setJsonObject(new JsonParser().parse(json).getAsJsonObject());

        assertNotNull(multiSearchResult.getResponses());
        assertEquals(3, multiSearchResult.getResponses().size());

        assertFalse(multiSearchResult.getResponses().get(0).isError);
        assertNull(multiSearchResult.getResponses().get(0).errorMessage);
        assertNull(multiSearchResult.getResponses().get(0).searchResult.getMaxScore());
        assertEquals(1, multiSearchResult.getResponses().get(0).searchResult.getHits(Map.class).size());

        assertFalse(multiSearchResult.getResponses().get(1).isError);
        assertNull(multiSearchResult.getResponses().get(1).errorMessage);
        assertNull(multiSearchResult.getResponses().get(1).searchResult.getMaxScore());
        assertEquals(0, multiSearchResult.getResponses().get(1).searchResult.getHits(Map.class).size());

        assertTrue(multiSearchResult.getResponses().get(2).isError);
        assertEquals("There was a \"test\" error", multiSearchResult.getResponses().get(2).errorMessage);
        assertNull(multiSearchResult.getResponses().get(2).searchResult);
    }
}
