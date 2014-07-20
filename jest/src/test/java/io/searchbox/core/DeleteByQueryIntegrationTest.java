package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numDataNodes = 1)
public class DeleteByQueryIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void delete() throws IOException {
        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";
        client.execute(new Index.Builder("\"user\":\"kimchy\"").index("twitter").type("tweet").id("1").build());
        DeleteByQuery deleteByQuery = new DeleteByQuery.Builder(query)
                .addIndex("twitter")
                .addType("tweet")
                .build();

        JestResult result = client.execute(deleteByQuery);
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        assertEquals(
                0,
                result.getJsonObject().getAsJsonObject("_indices").getAsJsonObject("twitter").getAsJsonObject("_shards").get("failed").getAsInt()
        );
    }

}
