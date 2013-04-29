package io.searchbox.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class JestResultTest {

    JestResult result = new JestResult();

    @Test
    public void extractGetResource() {
        String response = "{\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\", \n" +
                "    \"_source\" : {\n" +
                "        \"user\" : \"kimchy\",\n" +
                "        \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "        \"message\" : \"trying out Elastic Search\"\n" +
                "    }\n" +
                "}\n";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        Map<String, Object> expectedResultMap = new LinkedHashMap<String, Object>();
        expectedResultMap.put("user", "kimchy");
        expectedResultMap.put("postDate", "2009-11-15T14:12:12");
        expectedResultMap.put("message", "trying out Elastic Search");
        JsonObject actualResultMap =  result.extractSource().get(0).getAsJsonObject();
        assertEquals(expectedResultMap.size(), actualResultMap.entrySet().size());
        for (String key : expectedResultMap.keySet()) {
            assertEquals(expectedResultMap.get(key).toString(), actualResultMap.get(key).getAsString());
        }
    }

    @Test
    public void extractUnFoundGetResource() {
        String response = "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"13333\",\"exists\":false}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        JsonArray resultList =  result.extractSource();
        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    @Test
    public void getGetSourceAsObject() {
        String response = "{\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\", \n" +
                "    \"_source\" : {\n" +
                "        \"user\" : \"kimchy\",\n" +
                "        \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "        \"message\" : \"trying out Elastic Search\"\n" +
                "    }\n" +
                "}\n";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        Twitter twitter = result.getSourceAsObject(Twitter.class);
        assertNotNull(twitter);
        assertEquals("kimchy", twitter.getUser());
        assertEquals("trying out Elastic Search", twitter.getMessage());
        assertEquals("2009-11-15T14:12:12", twitter.getPostDate());
    }

    @Test
    public void getUnFoundGetResultAsAnObject() {
        String response = "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"13333\",\"exists\":false}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        assertNull(result.getSourceAsObject(Twitter.class));
    }


    @Test
    public void extractUnFoundMultiGetResource() {
        String response = "{\n" +
                "\n" +
                "\"docs\":\n" +
                "[\n" +
                "{\"_index\":\"test\",\"_type\":\"type\",\"_id\":\"1\",\"exists\":false},\n" +
                "{\"_index\":\"test\",\"_type\":\"type\",\"_id\":\"2\",\"exists\":false}\n" +
                "]\n" +
                "\n" +
                "}\n";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("docs/_source");

        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        JsonArray actual = result.extractSource();
        assertEquals(expected.size(), actual.size());
    }

    @Test
    public void extractMultiGetWithSourcePartlyFound() {
        String response = "{\"docs\":" +
                "[" +
                "{\"_index\":\"test\",\"_type\":\"type\",\"_id\":\"2\",\"exists\":false},\n" +
                "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"2\",\"_version\":2,\"exists\":true, " +
                "\"_source\" : {\n" +
                "    \"user\" : \"kimchy\",\n" +
                "    \"post_date\" : \"2009-11-15T14:12:12\",\n" +
                "    \"message\" : \"trying out Elastic Search\"\n" +
                "}}" +
                "]}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("docs/_source");
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        Map<String, Object> expectedMap1 = new LinkedHashMap<String, Object>();
        expectedMap1.put("user", "kimchy");
        expectedMap1.put("post_date", "2009-11-15T14:12:12");
        expectedMap1.put("message", "trying out Elastic Search");
        expected.add(expectedMap1);
        JsonArray actual = result.extractSource();
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            Map<String, Object> expectedMap = expected.get(i);
            JsonObject actualMap = actual.get(i).getAsJsonObject();
            for (String key : expectedMap.keySet()) {
                assertEquals(expectedMap.get(key).toString(), actualMap.get(key).getAsString());
            }
        }
    }

    @Test
    public void extractMultiGetWithSource() {
        String response = "{\"docs\":" +
                "[" +
                "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"1\",\"_version\":9,\"exists\":true, " +
                "\"_source\" : {\n" +
                "    \"user\" : \"kimchy\",\n" +
                "    \"post_date\" : \"2009-11-15T14:12:12\",\n" +
                "    \"message\" : \"trying out Elastic Search\"\n" +
                "}}," +
                "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"2\",\"_version\":2,\"exists\":true, " +
                "\"_source\" : {\n" +
                "    \"user\" : \"kimchy\",\n" +
                "    \"post_date\" : \"2009-11-15T14:12:12\",\n" +
                "    \"message\" : \"trying out Elastic Search\"\n" +
                "}}" +
                "]}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("docs/_source");

        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        Map<String, Object> expectedMap1 = new LinkedHashMap<String, Object>();
        expectedMap1.put("user", "kimchy");
        expectedMap1.put("post_date", "2009-11-15T14:12:12");
        expectedMap1.put("message", "trying out Elastic Search");

        Map<String, Object> expectedMap2 = new LinkedHashMap<String, Object>();
        expectedMap2.put("user", "kimchy");
        expectedMap2.put("post_date", "2009-11-15T14:12:12");
        expectedMap2.put("message", "trying out Elastic Search");

        expected.add(expectedMap1);
        expected.add(expectedMap2);

        JsonArray actual = result.extractSource();

        for (int i = 0; i < expected.size(); i++) {
            Map<String, Object> expectedMap = expected.get(i);
            JsonObject actualMap = actual.get(i).getAsJsonObject();
            for (String key : expectedMap.keySet()) {
                assertEquals(expectedMap.get(key).toString(), actualMap.get(key).getAsString());
            }
        }
    }

    @Test
    public void getMultiGetSourceAsObject() {
        String response = "{\"docs\":" +
                "[" +
                "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"1\",\"_version\":9,\"exists\":true, " +
                "\"_source\" : {\n" +
                "    \"user\" : \"kimchy\",\n" +
                "    \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "    \"message\" : \"trying out Elastic Search\"\n" +
                "}}," +
                "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"2\",\"_version\":2,\"exists\":true, " +
                "\"_source\" : {\n" +
                "    \"user\" : \"dogukan\",\n" +
                "    \"postDate\" : \"2012\",\n" +
                "    \"message\" : \"My message\"\n" +
                "}}" +
                "]}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("docs/_source");
        result.setSucceeded(true);

        List<Twitter> twitterList = result.getSourceAsObjectList(Twitter.class);

        assertEquals(2, twitterList.size());

        assertEquals("kimchy", twitterList.get(0).getUser());
        assertEquals("trying out Elastic Search", twitterList.get(0).getMessage());
        assertEquals("2009-11-15T14:12:12", twitterList.get(0).getPostDate());

        assertEquals("dogukan", twitterList.get(1).getUser());
        assertEquals("My message", twitterList.get(1).getMessage());
        assertEquals("2012", twitterList.get(1).getPostDate());
    }

    @Test
    public void getUnFoundMultiGetSourceAsObject() {
        String response = "{\n" +
                "\n" +
                "\"docs\":\n" +
                "[\n" +
                "{\"_index\":\"test\",\"_type\":\"type\",\"_id\":\"1\",\"exists\":false},\n" +
                "{\"_index\":\"test\",\"_type\":\"type\",\"_id\":\"2\",\"exists\":false}\n" +
                "]\n" +
                "\n" +
                "}\n";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("docs/_source");
        result.setSucceeded(true);
        List<Twitter> twitterList = result.getSourceAsObjectList(Twitter.class);
        assertEquals(0, twitterList.size());
    }


    @Test
    public void extractEmptySearchSource() {
        String response = "{\"took\":60,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1," +
                "\"failed\":0},\"hits\":{\"total\":0,\"max_score\":null,\"hits\":[]}}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("hits/hits/_source");
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        JsonArray actual =  result.extractSource();
        assertEquals(expected.size(), actual.size());
    }

    @Test
    public void extractSearchSource() {
        String response = "{\n" +
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
                "                \"_id\" : \"1\", \n" +
                "                \"_source\" : {\n" +
                "                    \"user\" : \"kimchy\",\n" +
                "                    \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "                    \"message\" : \"trying out Elastic Search\"\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("hits/hits/_source");
        Map<String, Object> expectedResultMap = new LinkedHashMap<String, Object>();
        expectedResultMap.put("user", "kimchy");
        expectedResultMap.put("postDate", "2009-11-15T14:12:12");
        expectedResultMap.put("message", "trying out Elastic Search");
        JsonObject actualResultMap = result.extractSource().get(0).getAsJsonObject();
        assertEquals(expectedResultMap.size() + 1, actualResultMap.entrySet().size());
        for (String key : expectedResultMap.keySet()) {
            assertEquals(expectedResultMap.get(key).toString(), actualResultMap.get(key).getAsString());
        }
    }

    @Test
    public void getSearchSourceAsObject() {
        String response = "{\n" +
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
                "                \"_id\" : \"1\", \n" +
                "                \"_source\" : {\n" +
                "                    \"user\" : \"kimchy\",\n" +
                "                    \"postDate\" : \"2009-11-15T14:12:12\",\n" +
                "                    \"message\" : \"trying out Elastic Search\"\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"_index\" : \"twitter\",\n" +
                "                \"_type\" : \"tweet\",\n" +
                "                \"_id\" : \"1\", \n" +
                "                \"_source\" : {\n" +
                "                    \"user\" : \"dogukan\",\n" +
                "                    \"postDate\" : \"2012\",\n" +
                "                    \"message\" : \"My Search Result\"\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("hits/hits/_source");
        result.setSucceeded(true);
        List<Twitter> twitterList = result.getSourceAsObjectList(Twitter.class);
        assertEquals(2, twitterList.size());
        assertEquals("kimchy", twitterList.get(0).getUser());
        assertEquals("trying out Elastic Search", twitterList.get(0).getMessage());
        assertEquals("2009-11-15T14:12:12", twitterList.get(0).getPostDate());
        assertEquals("dogukan", twitterList.get(1).getUser());
        assertEquals("My Search Result", twitterList.get(1).getMessage());
        assertEquals("2012", twitterList.get(1).getPostDate());
    }


    @Test
    public void extractIndexSource() {
        String response = "{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\"\n" +
                "}\n";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        Map<String, Object> expectedMap = new LinkedHashMap<String, Object>();
        expectedMap.put("ok", true);
        expectedMap.put("_index", "twitter");
        expectedMap.put("_type", "tweet");
        expectedMap.put("_id", "1");
        expected.add(expectedMap);
        JsonArray actual =  result.extractSource();
        for (int i = 0; i < expected.size(); i++) {
            Map<String, Object> map = expected.get(i);
            JsonObject actualMap = actual.get(i).getAsJsonObject();
            for (String key : map.keySet()) {
                assertEquals(map.get(key).toString(), actualMap.get(key).getAsString());
            }
        }
    }

    @Test
    public void extractCountResult() {
        String response = "{\n" +
                "    \"count\" : 1,\n" +
                "    \"_shards\" : {\n" +
                "        \"total\" : 5,\n" +
                "        \"successful\" : 5,\n" +
                "        \"failed\" : 0\n" +
                "    }\n" +
                "}\n";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("count");
        Double actual =  result.extractSource().get(0).getAsDouble();
        assertEquals(1.0, actual);
    }

    @Test
    public void getCountSourceAsObject() {
        String response = "{\n" +
                "    \"count\" : 1,\n" +
                "    \"_shards\" : {\n" +
                "        \"total\" : 5,\n" +
                "        \"successful\" : 5,\n" +
                "        \"failed\" : 0\n" +
                "    }\n" +
                "}\n";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("count");
        result.setSucceeded(true);
        Double count = result.getSourceAsObject(Double.class);
        assertEquals(1.0, count);
    }

    @Test
    public void getKeysWithPathToResult() {
        result.setPathToResult("_source");
        String[] expected = {"_source"};
        String[] actual = result.getKeys();
        assertEquals(1, actual.length);
        assertEquals(expected[0], actual[0]);
    }

    @Test
    public void getKeysWithoutPathToResult() {
        assertNull(result.getKeys());
    }

    class Twitter {
        String user;

        String postDate;

        String message;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPostDate() {
            return postDate;
        }

        public void setPostDate(String postDate) {
            this.postDate = postDate;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
