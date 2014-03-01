package io.searchbox.core;


import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class PercolateIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void percolateWithValidParameters() throws IOException {
        createIndex("cvbank");

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : {\n" +
                "            \"language\" : \"java\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        JestResult result = client.execute(new Index.Builder(query).index("_percolator").type("static").build());

        executeTestCase(new Percolate.Builder("cvbank", "candidate", "{\"doc\" : {\"language\":\"java\"}}").build());
    }

    private void executeTestCase(Percolate percolate) throws RuntimeException, IOException {
        JestResult result = client.execute(percolate);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

}