package io.searchbox.client;

import com.google.gson.JsonObject;
import io.searchbox.Action;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

public class AbstractJestClientTest {

    AbstractJestClient client = new AbstractJestClient() {
        @Override
        public JestResult execute(Action clientRequest) throws Exception {
            return null;  // NOOP
        }

        @Override
        public void executeAsync(Action clientRequest, JestResultHandler<JestResult> jestResultHandler) throws ExecutionException, InterruptedException, IOException {
            // NOOP
        }
    };

    @Test
    public void convertJsonStringToMapObject() {
        String json = "{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\"\n" +
                "}";
        JsonObject jsonMap = client.convertJsonStringToMapObject(json);
        assertNotNull(jsonMap);
        assertEquals(4, jsonMap.entrySet().size());
        assertEquals(true, jsonMap.get("ok").getAsBoolean());
        assertEquals("twitter", jsonMap.get("_index").getAsString());
        assertEquals("tweet", jsonMap.get("_type").getAsString());
        assertEquals("1", jsonMap.get("_id").getAsString());
    }

    @Test
    public void convertEmptyJsonStringToMapObject() {
        JsonObject jsonMap = client.convertJsonStringToMapObject("");
        assertNotNull(jsonMap);
    }

    @Test
    public void convertNullJsonStringToMapObject() {
        JsonObject jsonMap = client.convertJsonStringToMapObject(null);
        assertNotNull(jsonMap);
    }


    @Test
    public void getSuccessIndexResult() {
        String jsonString = "{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\"\n" +
                "}\n";
        Index index = new Index.Builder("{\"abc\":\"dce\"}").index("test").build();
        JestResult result = client.createNewElasticSearchResult(jsonString, 200, null, index);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void getFailedIndexResult() {
        String jsonString = "{\"error\":\"Invalid index\",\"status\":400}";
        Index index = new Index.Builder("{\"abc\":\"dce\"}").index("test").build();
        JestResult result = client.createNewElasticSearchResult(jsonString, 400, null, index);
        assertNotNull(result);
        assertFalse(result.isSucceeded());
        assertEquals("Invalid index", result.getErrorMessage());
    }

    @Test
    public void getSuccessDeleteResult() {
        String jsonString = "{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\",\n" +
                "    \"found\" : true\n" +
                "}\n";
        Delete delete = new Delete.Builder("1").index("twitter").type("tweet").build();
        JestResult result = client.createNewElasticSearchResult(jsonString, 200, null, delete);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void getFailedDeleteResult() {
        String jsonString = "{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\",\n" +
                "    \"found\" : false\n" +
                "}\n";
        Delete delete = new Delete.Builder("1").index("test").type("tweet").build();
        JestResult result = client.createNewElasticSearchResult(jsonString, 200, null, delete);
        assertNotNull(result);
        assertFalse(result.isSucceeded());
    }

    @Test
    public void getSuccessGetResult() {
        String jsonString = "{" +
                "    \"_index\" : \"twitter\"," +
                "    \"_type\" : \"tweet\"," +
                "    \"_id\" : \"1\"," +
                "    \"exists\" : true" +
                "}";
        Get get = new Get.Builder("test", "1").build();
        JestResult result = client.createNewElasticSearchResult(jsonString, 200, null, get);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
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
    }

    @Test
    public void getRequestURL() {
        String requestURI = "twitter/tweet/1";
        String elasticSearchServer = "http://localhost:9200";
        assertEquals("http://localhost:9200/twitter/tweet/1", client.getRequestURL(elasticSearchServer, requestURI));
    }

    @Test
    public void testGetElasticSearchServer() throws Exception {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        set.add("http://localhost:9200");
        set.add("http://localhost:9300");
        set.add("http://localhost:9400");
        client.setServers(set);

        Set<String> serverList = new HashSet<String>();

        for (int i = 0; i < 3; i++) {
            serverList.add(client.getElasticSearchServer());
        }

        assertEquals("round robin does not work", 3, serverList.size());

        assertTrue(set.contains("http://localhost:9200"));
        assertTrue(set.contains("http://localhost:9300"));
        assertTrue(set.contains("http://localhost:9400"));
    }
}
