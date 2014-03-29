package io.searchbox.core;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class ValidateIntegrationTest extends AbstractIntegrationTest {

    @Before
    public void setup() {
        createIndex("twitter");
    }

    @Test
    public void validateQueryWithIndex() throws IOException {
        Validate validate = new Validate.Builder("{\n" +
                "    \"query\" : {\n" +
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
                "    }\n" +
                "}")
                .index("twitter")
                .setParameter(Parameters.EXPLAIN, true)
                .build();
        executeTestCase(validate);
    }

    @Test
    public void validateQueryWithIndexAndType() throws IOException {
        executeTestCase(new Validate.Builder("{\n" +
                "    \"query\" : {\n" +
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
                "    }\n" +
                "}").index("twitter").type("tweet").build());
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue((Boolean) result.getValue("valid"));
        assertTrue(result.isSucceeded());
    }
}
