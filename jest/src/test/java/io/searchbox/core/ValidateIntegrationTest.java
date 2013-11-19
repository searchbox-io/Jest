package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchMapping;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class ValidateIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void validateQueryWithIndex() {
        try {
            Validate validate = new Validate.Builder("{\n" +
                    "  \"filtered\" : {\n" +
                    "    \"query\" : {\n" +
                    "      \"query_string\" : {\n" +
                    "        \"query\" : \"*:*\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"filter\" : {\n" +
                    "      \"term\" : { \"user\" : \"kimchy\" }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}")
                    .index("twitter")
                    .setParameter(Parameters.EXPLAIN, true)
                    .build();
            executeTestCase(validate);
        } catch (IOException e) {
            fail("Failed during the validate query with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter",
            mappings = {
                    @ElasticsearchMapping(typeName = "tweet")
            })

    public void validateQueryWithIndexAndType() {
        try {
            executeTestCase(new Validate.Builder("{\n" +
                    "  \"filtered\" : {\n" +
                    "    \"query\" : {\n" +
                    "      \"query_string\" : {\n" +
                    "        \"query\" : \"*:*\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"filter\" : {\n" +
                    "      \"term\" : { \"user\" : \"kimchy\" }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}").index("twitter").type("tweet").build());
        } catch (IOException e) {
            fail("Failed during the validate query with valid parameters. Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue((Boolean) result.getValue("valid"));
        assertTrue(result.isSucceeded());
    }
}
