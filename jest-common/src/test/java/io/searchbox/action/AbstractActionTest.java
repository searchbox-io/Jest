package io.searchbox.action;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.searchbox.annotations.JestId;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ElasticsearchVersion;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Update;
import io.searchbox.indices.Flush;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Dogukan Sonmez
 */
public class AbstractActionTest {

    @Test
    public void buildRestUrlWithValidParameters() {
        String expected = "twitter/tweet/1";
        final Delete build = new Delete.Builder("1").index("twitter").type("tweet").build();
        String actual = build.buildURI(ElasticsearchVersion.UNKNOWN);
        assertEquals(expected, actual);
    }

    @Test
    public void buildUrlWithRequestParameterWithMultipleValues() {
        DummyAction dummyAction = new DummyAction.Builder()
                .setParameter("x", "y")
                .setParameter("x", "z")
                .setParameter("x", "q")
                .setParameter("w", "p")
                .build();
        assertEquals("?x=y&x=z&x=q&w=p", dummyAction.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testEqualsAndHashcode() {
        Action dummyAction1 = new DummyAction.Builder()
                .setParameter("x", "y")
                .setParameter("x", "z")
                .setHeader("X-Custom-Header", "hatsune")
                .build();

        Action dummyAction2 = new DummyAction.Builder()
                .setParameter("x", "y")
                .setParameter("x", "z")
                .setHeader("X-Custom-Header", "hatsune")
                .build();

        Action dummyAction3 = new DummyAction.Builder()
                .setParameter("x", "1")
                .setParameter("x", "z")
                .setHeader("X-Custom_Header", "hatsune")
                .build();

        Action flush = new Flush.Builder().build();

        assertTrue(dummyAction1.equals(dummyAction2));
        assertTrue(dummyAction2.equals(dummyAction1));
        assertEquals(dummyAction1, dummyAction2);
        assertEquals(dummyAction1.hashCode(), dummyAction2.hashCode());

        assertFalse(dummyAction3.equals(dummyAction1));
        assertFalse(dummyAction3.equals(dummyAction2));
        assertFalse(dummyAction1.equals(dummyAction3));
        assertFalse(dummyAction2.equals(dummyAction3));
        assertNotEquals(dummyAction1.hashCode(), dummyAction3.hashCode());
        assertNotEquals(dummyAction2.hashCode(), dummyAction3.hashCode());

        assertFalse(dummyAction1.equals(flush));
        assertFalse(dummyAction2.equals(flush));
        assertFalse(dummyAction3.equals(flush));
        assertNotEquals(dummyAction1.hashCode(), flush.hashCode());
        assertNotEquals(dummyAction2.hashCode(), flush.hashCode());
        assertNotEquals(dummyAction3.hashCode(), flush.hashCode());
    }

    @Test
    public void restMethodNameMultipleClientRequest() {
        Get get = new Get.Builder("twitter", "1").type("tweet").build();
        assertEquals("GET", get.getRestMethodName());

        Delete del = new Delete.Builder("1").index("twitter").type("tweet").build();
        assertEquals("DELETE", del.getRestMethodName());
    }

    @Test
    public void requestDataMultipleClientRequest() {
        Index indexDocument = new Index.Builder("\"indexDocumentData\"").index("index").type("type").id("id").build();
        Update update = new Update.Builder("\"updateData\"").index("indexName").type("indexType").id("1").build();

        assertEquals("\"updateData\"", update.getData(null).toString());
        assertEquals("POST", update.getRestMethodName());
        assertEquals("indexName/indexType/1/_update", update.getURI(ElasticsearchVersion.UNKNOWN));

        assertEquals("\"indexDocumentData\"", indexDocument.getData(null).toString());
        assertEquals("PUT", indexDocument.getRestMethodName());
        assertEquals("index/type/id", indexDocument.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void getIdFromNullSource() {
        assertNull(AbstractAction.getIdFromSource(null));
    }

    @Test
    public void getIdFromSourceWithoutAnnotation() {
        assertNull(AbstractAction.getIdFromSource("JEST"));
    }

    @Test
    public void getIdFromSourceWithAnnotation() {
        String expected = "jest@searchbox.io";
        String actual = AbstractAction.getIdFromSource(new Source("data", "jest@searchbox.io"));
        assertEquals(expected, actual);
    }

    @Test
    public void getIdFromSourceWithAnnotationWithNullId() {
        assertNull(AbstractAction.getIdFromSource(new Source("data", null)));
    }

    static class DummyAction extends GenericResultAbstractAction {
        public DummyAction(Builder builder) {
            super(builder);
        }

        @Override
        public String getRestMethodName() {
            return "GET";
        }

        public static class Builder extends AbstractAction.Builder<DummyAction, Builder> {

            @Override
            public DummyAction build() {
                return new DummyAction(this);
            }
        }
    }

    @Test
    public void convertJsonStringToMapObject() {
        String json = "{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\"\n" +
                "}";
        JsonObject jsonMap = new DummyAction.Builder().build().parseResponseBody(json);
        assertNotNull(jsonMap);
        assertEquals(4, jsonMap.entrySet().size());
        assertEquals(true, jsonMap.get("ok").getAsBoolean());
        assertEquals("twitter", jsonMap.get("_index").getAsString());
        assertEquals("tweet", jsonMap.get("_type").getAsString());
        assertEquals("1", jsonMap.get("_id").getAsString());
    }

    @Test
    public void convertEmptyJsonStringToMapObject() {
        JsonObject jsonMap = new DummyAction.Builder().build().parseResponseBody("");
        assertNotNull(jsonMap);
    }

    @Test
    public void convertNullJsonStringToMapObject() {
        JsonObject jsonMap = new DummyAction.Builder().build().parseResponseBody(null);
        assertNotNull(jsonMap);
    }

    @Test(expected = JsonSyntaxException.class)
    public void propagateExceptionWhenTheResponseIsNotJson1() {
        new DummyAction.Builder().build().parseResponseBody("401 Unauthorized");
    }

    @Test(expected = JsonSyntaxException.class)
    public void propagateExceptionWhenTheResponseIsNotJson2() {
        new DummyAction.Builder().build().parseResponseBody("banana");
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
        JestResult result = index.createNewElasticSearchResult(jsonString, 200, null, new Gson());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(200, result.getResponseCode());
    }

    @Test
    public void getFailedIndexResult() {
        String jsonString = "{\"error\":\"Invalid index\",\"status\":400}";
        Index index = new Index.Builder("{\"abc\":\"dce\"}").index("test").build();
        JestResult result = index.createNewElasticSearchResult(jsonString, 400, null, new Gson());
        assertFalse(result.isSucceeded());
        assertEquals("\"Invalid index\"", result.getErrorMessage());
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
        JestResult result = delete.createNewElasticSearchResult(jsonString, 200, null, new Gson());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    //TODO: This cannot be derived from the result anymore
    @Test
    public void getFailedDeleteResult() {
        String jsonString = "{\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\",\n" +
                "    \"found\" : false\n" +
                "}\n";
        Delete delete = new Delete.Builder("1").index("test").type("tweet").build();
        JestResult result = delete.createNewElasticSearchResult(jsonString, 404, null, new Gson());
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
        JestResult result = get.createNewElasticSearchResult(jsonString, 200, null, new Gson());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    class Source {

        @JestId
        String email;
        String data;

        Source(String data, String email) {
            this.data = data;
            this.email = email;
        }
    }

}
