package io.searchbox.core;

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
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numDataNodes = 1)
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

        Explain explain = new Explain.Builder("twitter", "tweet", "1", query).build();
        DocumentResult result = client.execute(explain);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals("twitter", result.getIndex());
        assertEquals("tweet", result.getType());
        assertEquals("1", result.getId());
    }
}
