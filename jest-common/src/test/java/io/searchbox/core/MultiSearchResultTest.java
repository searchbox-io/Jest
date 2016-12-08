package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.util.Map;

import static com.spatial4j.core.io.jts.JtsWktShapeParser.ValidationRule.error;
import static org.junit.Assert.*;

public class MultiSearchResultTest {
    @Test
    public void testMultiSearchResult() {
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
