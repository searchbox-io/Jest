package io.searchbox.core;

import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.test.ESIntegTestCase;
import org.json.JSONException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class ExplainIntegrationTest extends AbstractIntegrationTest {

    final static Logger log = LoggerFactory.getLogger(ExplainIntegrationTest.class);

    @Test
    public void explain() throws IOException, JSONException {

        client().index(new IndexRequest("twitter", "tweet", "1").source("user", "tweety").setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet();
        String query = "{\n" +
                "    \"query\": {\n" +
                "                \"query_string\" : {\n" +
                "                    \"query\" : \"test\"\n" +
                "                }\n" +
                "            }\n" +
                "}";

        Explain explain = new Explain.Builder("twitter", "tweet", "1", query).build();
        DocumentResult result = client.execute(explain);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals("twitter", result.getIndex());
        assertEquals("tweet", result.getType());
        assertEquals("1", result.getId());
    }
}
