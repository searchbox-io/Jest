package io.searchbox.client;

import com.google.gson.Gson;
import io.searchbox.Document;
import io.searchbox.Source;
import io.searchbox.client.http.ElasticSearchHttpClient;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
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
    public void createNewElasticSearchResultWithValidParameters() {
        Map jsonMap = new HashMap();
        jsonMap.put("ok", true);
        jsonMap.put("_index", "twitter");
        jsonMap.put("_type", "tweet");
        jsonMap.put("_id", "1");
        StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "");
        ElasticSearchResult result = client.createNewElasticSearchResult(jsonMap, statusLine, "INDEX");
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void isOperationSucceedWithTrueIndex() {
        Map jsonMap = new HashMap();
        jsonMap.put("ok", true);
        assertTrue(client.isOperationSucceed(jsonMap, "INDEX"));
    }

    @Test
    public void isOperationSucceedWithFalseIndex() {
        Map jsonMap = new HashMap();
        jsonMap.put("ok", false);
        assertFalse(client.isOperationSucceed(jsonMap, "INDEX"));
    }

    @Test
    public void isOperationSucceedWithUnExpectedIndexResult() {
        Map jsonMap = new HashMap();
        assertTrue(client.isOperationSucceed(jsonMap, "INDEX"));
    }

    @Test
    public void isOperationSucceedWithDelete() {
        Map jsonMap = new HashMap();
        jsonMap.put("ok", true);
        jsonMap.put("found", true);
        assertTrue(client.isOperationSucceed(jsonMap, "DELETE"));
    }

    @Test
    public void isOperationSucceedWithUnFoundDelete() {
        Map jsonMap = new HashMap();
        jsonMap.put("ok", true);
        jsonMap.put("found", false);
        assertFalse(client.isOperationSucceed(jsonMap, "DELETE"));
    }

    @Test
    public void isOperationSucceedWithUnExpectedDelete() {
        Map jsonMap = new HashMap();
        jsonMap.put("ok", true);
        assertTrue(client.isOperationSucceed(jsonMap, "DELETE"));
    }

    @Test
    public void isOperationSucceedWithWrongDelete() {
        Map jsonMap = new HashMap();
        assertTrue(client.isOperationSucceed(jsonMap, "DELETE"));
    }

    @Test
    public void isOperationSucceedWithUpdate() {
        Map jsonMap = new HashMap();
        jsonMap.put("ok", true);
        assertTrue(client.isOperationSucceed(jsonMap, "Update"));
    }

    @Test
    public void isOperationSucceedWithUnExpectedUpdate() {
        Map jsonMap = new HashMap();
        assertTrue(client.isOperationSucceed(jsonMap, "Update"));
    }

    @Test
    public void isOperationSucceedWithGet() {
        Map jsonMap = new HashMap();
        jsonMap.put("exists", true);
        assertTrue(client.isOperationSucceed(jsonMap, "GET"));
    }

    @Test
    public void isOperationSucceedWithFalseGet() {
        Map jsonMap = new HashMap();
        jsonMap.put("exists", false);
        assertFalse(client.isOperationSucceed(jsonMap, "GET"));
    }


    @Test
    public void extractDocumentsFromResponseForGetRequest() {
        Map jsonMap = new HashMap();
        jsonMap.put("ok", true);
        jsonMap.put("_index", "twitter");
        jsonMap.put("_type", "tweet");
        jsonMap.put("_id", "1");
        jsonMap.put("_source", "{user:\"searchboxio\"}");
        List<Document> documents = client.extractDocumentsFromResponse(jsonMap, "Get");
        assertEquals(1, documents.size());
        Document document = documents.get(0);
        assertEquals("twitter", document.getIndexName());
        assertEquals("tweet", document.getType());
        assertEquals("1", document.getId());
        assertEquals(new Source("{user:\"searchboxio\"}").toString(), document.getSource().toString());
    }

    @Test
    public void extractDocumentsFromResponseForSearchRequest() {
        String searchResult = "{\n" +
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
        Map jsonMap = new Gson().fromJson(searchResult, Map.class);
        List<Document> documents = client.extractDocumentsFromResponse(jsonMap, "Search");
        assertEquals(1, documents.size());
        Document document = documents.get(0);
        assertEquals("twitter", document.getIndexName());
        assertEquals("tweet", document.getType());
        assertEquals("1", document.getId());
        assertEquals("{\"user\":\"kimchy\",\"postDate\":\"2009-11-15T14:12:12\",\"message\":\"trying out Elastic Search\"}", document.getSource().toString());
    }
}
