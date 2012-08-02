package io.searchbox.client;

import com.google.gson.Gson;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchResultTest {

    ElasticSearchResult result = new ElasticSearchResult();

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
        Map<String, Object> actualResultMap = ((List<Map<String, Object>>) result.extractSource()).get(0);
        assertEquals(expectedResultMap.size(), actualResultMap.size());
        for (String key : expectedResultMap.keySet()) {
            assertEquals(expectedResultMap.get(key), actualResultMap.get(key));
        }
    }

    @Test
    public void extractMultiGetResource() {
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
        List<Map<String, Object>> actual = (List<Map<String, Object>>) result.extractSource();
        assertEquals(expected.size(), actual.size());
    }

    @Test
    public void extractMultiGetWithExistingSource() {
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

        List<Map<String, Object>> actual = (List<Map<String, Object>>) result.extractSource();

        for (int i = 0; i < expected.size(); i++) {
            Map<String, Object> expectedMap = expected.get(i);
            Map<String, Object> actualMap = actual.get(i);
            for (String key : expectedMap.keySet()) {
                assertEquals(expectedMap.get(key), actualMap.get(key));
            }
        }
    }

    @Test
    public void extractEmptySearchSource() {
        String response = "{\"took\":60,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1," +
                "\"failed\":0},\"hits\":{\"total\":0,\"max_score\":null,\"hits\":[]}}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("hits/hits/_source");
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> actual = (List<Map<String, Object>>) result.extractSource();
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
        Map<String, Object> actualResultMap = ((List<Map<String, Object>>) result.extractSource()).get(0);
        assertEquals(expectedResultMap.size(), actualResultMap.size());
        for (String key : expectedResultMap.keySet()) {
            assertEquals(expectedResultMap.get(key), actualResultMap.get(key));
        }
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
        List<Map<String, Object>> actual = (List<Map<String, Object>>) result.extractSource();
        for (int i = 0; i < expected.size(); i++) {
            Map<String, Object> map = expected.get(i);
            Map<String, Object> actualMap = actual.get(i);
            for (String key : map.keySet()) {
                assertEquals(map.get(key), actualMap.get(key));
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
        Double actual = (Double) ((List) result.extractSource()).get(0);
        assertEquals(1, actual);
    }

    @Test
    public void getSourceAsObject() {
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
        Double count = (Double) result.getSourceAsObject(Double.class);
        assertEquals(1.0, count);
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
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("hits/hits/_source");
        Twitter twitter = (Twitter) result.getSourceAsObject(Twitter.class);
        assertNotNull(twitter);
    }

    class Twitter{
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

}
