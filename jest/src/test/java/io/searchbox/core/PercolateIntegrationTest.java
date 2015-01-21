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
public class PercolateIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void percolateWithValidParameters() throws IOException {
        String index = "cvbank";
        createIndex(index);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : {\n" +
                "            \"language\" : \"java\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        // register percolator query on our index
        JestResult result = client.execute(new Index.Builder(query).index(index).type(".percolator").id("1").build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        // try to match a document against the registered percolator
        Percolate percolate = new Percolate.Builder(index, "candidate", "{\"doc\" : {\"language\":\"java\"}}").build();
        result = client.execute(percolate);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1, result.getJsonObject().getAsJsonPrimitive("total").getAsInt());
    }
}