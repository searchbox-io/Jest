package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 1)
public class DeleteByQueryIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void delete() throws IOException, InterruptedException {
        final String index = "twitter";
        final String type = "tweet";
        final String id = "1";
        final String query = "{\n" +
                "    \"query\": {\n" +
                "        \"term\": { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";

        assertTrue(index(index, type, id, "{\"user\":\"kimchy\"}").isCreated());
        refresh();
        ensureSearchable(index);

        DeleteByQuery deleteByQuery = new DeleteByQuery.Builder(query)
                .addIndex("twitter")
                .addType("tweet")
                .build();

        JestResult result = client.execute(deleteByQuery);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        assertEquals(
                0,
                result.getJsonObject().getAsJsonObject("_indices").getAsJsonObject("twitter").getAsJsonObject("_shards").get("failed").getAsInt()
        );
    }

}
