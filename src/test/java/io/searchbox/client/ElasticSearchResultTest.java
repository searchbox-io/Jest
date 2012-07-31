package io.searchbox.client;

import com.google.gson.Gson;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchResultTest {

    ElasticSearchResult result = new ElasticSearchResult();

    @Test
    public void extractGetResource(){
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
        Map<String,Object> expectedResultMap = new LinkedHashMap<String,Object>();
        expectedResultMap.put("user","kimchy");
        expectedResultMap.put("postDate","2009-11-15T14:12:12");
        expectedResultMap.put("message","trying out Elastic Search");
        Map<String,Object> actualResultMap = result.extractSource().get(0);
        assertEquals(expectedResultMap.size(),actualResultMap.size());
        for(String key:expectedResultMap.keySet()){
             assertEquals(expectedResultMap.get(key),actualResultMap.get(key));
        }
    }
    @Test
    public void extractMultiGetResource(){
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
        result.setPathToResult("docs");

        List<Map<String,Object>> expected = new ArrayList<Map<String, Object>>();
        Map<String,Object> expectedResultMap = new LinkedHashMap<String,Object>();
        expectedResultMap.put("user","kimchy");
        expectedResultMap.put("postDate","2009-11-15T14:12:12");
        expectedResultMap.put("message","trying out Elastic Search");
        expected.add(expectedResultMap);

        List<Map<String,Object>>  actual = result.extractSource();

        assertEquals(expected.size(),actual.size());
        for(int i = 0;i<expected.size();i++){
            Map<String,Object> expectedMap = expected.get(i);
            Map<String,Object> actualMap = actual.get(i);
            for(String key:expectedMap.keySet()){
                assertEquals(expectedMap.get(key),actualMap.get(key));
            }
        }
    }


    @Test
    public void getKeysWithPathToResult(){
        result.setPathToResult("_source");
        String[] expected = {"_source"};
        String[] actual = result.getKeys();
        assertEquals(1,actual.length);
        assertEquals(expected[0],actual[0]);
    }

    @Test
    public void getKeysWithoutPathToResult(){
        String[] expected = {"null"};
        String[] actual = result.getKeys();
        assertEquals(1,actual.length);
        assertEquals(expected[0],actual[0]);
    }
}
