package io.searchbox.core;


import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numDataNodes = 1)
public class UpdateIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void updateWithValidParameters() throws Exception {
        String script = "{\n" +
                "    \"lang\" : \"groovy\",\n" +
                "    \"script\" : \"ctx._source.tags += tag\",\n" +
                "    \"params\" : {\n" +
                "        \"tag\" : \"blue\"\n" +
                "    }\n" +
                "}";

        client().index(
                new IndexRequest("twitter", "tweet", "1")
                        .source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}")
                        .refresh(true)
        ).actionGet();

        DocumentResult result = client.execute(new Update.Builder(script).index("twitter").type("tweet").id("1").build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals("twitter", result.getIndex());
        assertEquals("tweet", result.getType());
        assertEquals("1", result.getId());

        DocumentResult getResult = client.execute(new Get.Builder("twitter", "1").type("tweet").build());
        assertEquals("That is testblue", ((Map) getResult.getValue("_source")).get("tags"));
    }
}
