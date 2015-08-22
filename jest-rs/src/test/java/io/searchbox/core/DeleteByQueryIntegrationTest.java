package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Ignored as <a href=
 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete-by-query.html">
 * Delete By Query API</a> will be deprecated. <blockquote>Warning Deprecated in
 * 1.5.3. "Delete by Query will be removed in 2.0: it is problematic since it
 * silently forces a refresh which can quickly cause OutOfMemoryError during
 * concurrent indexing, and can also cause primary and replica to become
 * inconsistent. Instead, use the scroll/scan API to find all matching ids and
 * then issue a bulk request to delete them.</blockquote>
 * 
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numDataNodes = 1)
@Ignore
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
        assertTrue(result.getErrorMessage(), result.isSucceeded());;

        assertEquals(
                0,
                result.getJsonObject().getAsJsonObject("_indices").getAsJsonObject("twitter").getAsJsonObject("_shards").get("failed").getAsInt()
        );
    }

}
