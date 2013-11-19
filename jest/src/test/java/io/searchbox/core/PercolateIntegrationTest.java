package io.searchbox.core;


import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class PercolateIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "cvbank")
    public void percolateWithValidParameters() {
        try {

            String query = "{\n" +
                    "    \"query\" : {\n" +
                    "        \"term\" : {\n" +
                    "            \"language\" : \"java\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

            JestResult result = client.execute(new Index.Builder(query).index("_percolator").type("static").build());

            executeTestCase(new Percolate.Builder("cvbank", "candidate", "{\"doc\" : {\"language\":\"java\"}}").build());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    private void executeTestCase(Percolate percolate) throws RuntimeException, IOException {
        JestResult result = client.execute(percolate);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        assertEquals(true, result.getValue("ok"));
    }

}