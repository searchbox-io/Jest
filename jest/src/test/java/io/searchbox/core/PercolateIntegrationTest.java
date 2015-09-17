package io.searchbox.core;


import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;

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

        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(
                new PutMappingRequest(index).type(type).source(
                        "{ \"properties\" : { \"language\" : {\"type\" : \"string\", \"store\" : \"yes\"} } }"
                )
        ).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());

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
        Percolate percolate = new Percolate.Builder(index, type, "{\"doc\" : {\"language\":\"java\"}}").build();
        result = client.execute(percolate);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1, result.getJsonObject().getAsJsonPrimitive("total").getAsInt());
    }
}