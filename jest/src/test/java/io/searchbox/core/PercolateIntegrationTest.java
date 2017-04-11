package io.searchbox.core;


import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;

import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertAcked;

/**
 * @author Dogukan Sonmez
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class PercolateIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void percolateWithValidParameters() throws IOException {
        String index = "cvbank";
        String type = "candidate";
        createIndex(index);
        ensureSearchable(index);

        assertAcked(client().admin().indices().preparePutMapping(index)
                .setType(type)
                .setSource("{\"properties\":{\"language\":{\"type\":\"keyword\",\"store\":\"yes\"}}}")
                .get());
        assertAcked(client().admin().indices().preparePutMapping(index)
                .setType("queries")
                .setSource("{\"properties\":{\"query\":{\"type\":\"percolator\"}}}")
                .get());

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : {\n" +
                "            \"language\" : \"java\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        // register percolator query on our index
        JestResult result = client.execute(new Index.Builder(query).index(index).type("queries").id("1").refresh(true).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        // try to match a document against the registered percolator
        Percolate percolate = new Percolate.Builder(index, type, "{\"doc\" : {\"language\":\"java\"}}").build();
        result = client.execute(percolate);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1, result.getJsonObject().getAsJsonPrimitive("total").getAsInt());
    }
}