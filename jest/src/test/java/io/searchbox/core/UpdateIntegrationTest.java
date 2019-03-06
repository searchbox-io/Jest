package io.searchbox.core;


import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

/**
 * @author Dogukan Sonmez
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class UpdateIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX = "twitter";
    private static final String TYPE = "tweet";

    @Test
    public void scriptedUpdateWithValidParameters() throws Exception {
        String id = "1";
        String script = "{\n" +
                "  \"script\": {\n" +
                "    \"lang\": \"painless\",\n" +
                "    \"inline\": \"ctx._source.tags += params.tag\",\n" +
                "    \"params\": {\n" +
                "      \"tag\": \"blue\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        client().index(
                new IndexRequest(INDEX, TYPE, id)
                        .source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}", XContentType.JSON)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
        ).actionGet();

        DocumentResult result = client.execute(new Update.Builder(script).index(INDEX).type(TYPE).id(id).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(INDEX, result.getIndex());
        assertEquals(TYPE, result.getType());
        assertEquals(id, result.getId());

        GetResponse getResult = get(INDEX, TYPE, id);
        assertTrue(getResult.isExists());
        assertFalse(getResult.isSourceEmpty());
        assertEquals("That is testblue", getResult.getSource().get("tags"));
    }

    @Test
    public void scriptedUpdateWithValidParametersUsingScriptBuilder() throws Exception {
        String id = "1";
        String script = "{\n" +
                "  \"lang\": \"painless\",\n" +
                "  \"inline\": \"ctx._source.tags += params.tag\",\n" +
                "  \"params\": {\n" +
                "    \"tag\": \"blue\"\n" +
                "  }\n" +
                "}";

        client().index(
                new IndexRequest(INDEX, TYPE, id)
                        .source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}", XContentType.JSON)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
        ).actionGet();

        DocumentResult result = client.execute(new Update.ScriptBuilder(script).index(INDEX).type(TYPE).id(id).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(INDEX, result.getIndex());
        assertEquals(TYPE, result.getType());
        assertEquals(id, result.getId());

        GetResponse getResult = get(INDEX, TYPE, id);
        assertTrue(getResult.isExists());
        assertFalse(getResult.isSourceEmpty());
        assertEquals("That is testblue", getResult.getSource().get("tags"));
    }

    @Test
    public void partialDocUpdateWithValidParameters() throws Exception {
        String id = "2";
        String partialDoc = "{\n" +
                "    \"doc\" : {\n" +
                "        \"tags\" : \"blue\"\n" +
                "    }\n" +
                "}";

        client().index(
                new IndexRequest(INDEX, TYPE, id)
                        .source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}", XContentType.JSON)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
        ).actionGet();

        DocumentResult result = client.execute(new Update.Builder(partialDoc).index(INDEX).type(TYPE).id(id).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(INDEX, result.getIndex());
        assertEquals(TYPE, result.getType());
        assertEquals(id, result.getId());

        GetResponse getResult = get(INDEX, TYPE, id);
        assertTrue(getResult.isExists());
        assertFalse(getResult.isSourceEmpty());
        assertEquals("blue", getResult.getSource().get("tags"));
    }

    @Test
    public void partialDocUpdateWithValidParametersUsingDocBuilder() throws Exception {
        String id = "2";
        String partialDoc = "{\n" +
                "  \"tags\" : \"blue\"\n" +
                "}";

        client().index(
                new IndexRequest(INDEX, TYPE, id)
                        .source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}", XContentType.JSON)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
        ).actionGet();

        DocumentResult result = client.execute(new Update.DocBuilder(partialDoc).index(INDEX).type(TYPE).id(id).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(INDEX, result.getIndex());
        assertEquals(TYPE, result.getType());
        assertEquals(id, result.getId());

        GetResponse getResult = get(INDEX, TYPE, id);
        assertTrue(getResult.isExists());
        assertFalse(getResult.isSourceEmpty());
        assertEquals("blue", getResult.getSource().get("tags"));
    }

    @Test
    public void partialDocUpdateWithValidVersion() throws Exception {
        String id = "2";
        String partialDoc = "{\n" +
                "    \"doc\" : {\n" +
                "        \"tags\" : \"blue\"\n" +
                "    }\n" +
                "}";

        IndexResponse response = client().index(
                new IndexRequest(INDEX, TYPE, id)
                        .source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}", XContentType.JSON)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
        ).actionGet();
        long version = response.getVersion();

        DocumentResult result = client.execute(new Update.VersionBuilder(partialDoc, version)
                .index(INDEX)
                .type(TYPE)
                .id(id)
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(INDEX, result.getIndex());
        assertEquals(TYPE, result.getType());
        assertEquals(id, result.getId());
        assertEquals(version + 1, result.getVersion().longValue());

        GetResponse getResult = get(INDEX, TYPE, id);
        assertTrue(getResult.isExists());
        assertFalse(getResult.isSourceEmpty());
        assertEquals("blue", getResult.getSource().get("tags"));
    }

    @Test
    public void partialDocUpdateWithInvalidVersion() throws Exception {
        String id = "2";
        String partialDoc = "{\n" +
                "    \"doc\" : {\n" +
                "        \"tags\" : \"blue\"\n" +
                "    }\n" +
                "}";
        String partialDoc2 = "{\n" +
                "    \"doc\" : {\n" +
                "        \"tags\" : \"red\"\n" +
                "    }\n" +
                "}";

        IndexResponse response = client().index(
                new IndexRequest(INDEX, TYPE, id)
                        .source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}", XContentType.JSON)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
        ).actionGet();
        long version = response.getVersion();

        DocumentResult result = client.execute(new Update.VersionBuilder(partialDoc, version)
                .index(INDEX)
                .type(TYPE)
                .id(id)
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        // and again ...
        result = client.execute(new Update.VersionBuilder(partialDoc2, version)
                .index(INDEX)
                .type(TYPE)
                .id(id)
                .build());

        assertFalse(result.getErrorMessage(), result.isSucceeded());
        assertEquals("Invalid response code", RestStatus.CONFLICT.getStatus(), result.getResponseCode());

        GetResponse getResult = get(INDEX, TYPE, id);
        assertTrue(getResult.isExists());
        assertEquals(version + 1, getResult.getVersion());
        assertEquals("blue", getResult.getSource().get("tags"));
    }

}
