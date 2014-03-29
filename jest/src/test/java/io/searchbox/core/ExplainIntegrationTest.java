package io.searchbox.core;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class ExplainIntegrationTest extends AbstractIntegrationTest {

    final static Logger log = LoggerFactory.getLogger(ExplainIntegrationTest.class);

    @Test
    public void explain() throws IOException {
        client().index(new IndexRequest("twitter", "tweet", "1").source("{\"user\":\"tweety\"}").refresh(true)).actionGet();

        String query = "{\n" +
                "    \"query\": {\n" +
                "        \"filtered\" : {\n" +
                "            \"query\" : {\n" +
                "                \"query_string\" : {\n" +
                "                    \"query\" : \"test\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        executeTestCase(new Explain.Builder("twitter", "tweet", "1", query).build());
        log.info("Successfully finished explain operation");
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

}
