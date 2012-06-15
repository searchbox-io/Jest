package io.searchbox.client;

import io.searchbox.client.http.ElasticSearchHttpClient;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class AbstractElasticSearchClientTest {

    ElasticSearchHttpClient client = new ElasticSearchHttpClient();

    @Test
    public void convertJsonStringToMapObject() {
        String json = "{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\"\n" +
                "}";
        Map jsonMap = client.convertJsonStringToMapObject(json);
        assertNotNull(jsonMap);
        assertEquals(4, jsonMap.size());
        assertEquals(true, jsonMap.get("ok"));
        assertEquals("twitter", jsonMap.get("_index"));
        assertEquals("tweet", jsonMap.get("_type"));
        assertEquals("1", jsonMap.get("_id"));
    }

    @Test
    public void convertEmptyJsonStringToMapObject() {
        Map jsonMap = client.convertJsonStringToMapObject("");
        assertNull(jsonMap);
    }

    @Test
    public void convertNullJsonStringToMapObject() {
        Map jsonMap = client.convertJsonStringToMapObject(null);
        assertNull(jsonMap);
    }


    @Test
    public void createNewElasticSearchResultWithValidParameters(){
        Map jsonMap = new HashMap();
        jsonMap.put("ok","true");
        jsonMap.put("_index","twitter");
        jsonMap.put("_type","tweet");
        jsonMap.put("_id","1");
        StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),200,"");
        ElasticSearchResult result = client.createNewElasticSearchResult(jsonMap,statusLine);
        assertNotNull(result);
        assertEquals("1",result.getId());
        assertEquals("tweet",result.getType());
        assertEquals("twitter",result.getIndexName());
        assertTrue(result.isSucceeded());
    }


}
