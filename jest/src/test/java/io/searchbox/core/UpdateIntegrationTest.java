package io.searchbox.core;


import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.util.Map;

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
                "    \"lang\" : \"groovy\",\n" +
                "    \"script\" : \"ctx._source.tags += tag\",\n" +
                "    \"params\" : {\n" +
                "        \"tag\" : \"blue\"\n" +
                "    }\n" +
                "}";

        client().index(
                new IndexRequest(INDEX, TYPE, id)
                        .source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}")
                        .refresh(true)
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
    public void partialDocUpdateWithValidParameters() throws Exception {
        String id = "2";
        String partialDoc = "{\n" +
                "    \"doc\" : {\n" +
                "        \"tags\" : \"blue\"\n" +
                "    }\n" +
                "}";

        client().index(
                new IndexRequest(INDEX, TYPE, id)
                        .source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}")
                        .refresh(true)
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
}
