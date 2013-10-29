package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class ExplainIntegrationTest extends AbstractIntegrationTest {

    final static Logger log = LoggerFactory.getLogger(ExplainIntegrationTest.class);

    @Before
    public void before() throws Exception {
        Index index = new Index.Builder("{\"user\":\"tweety\"}")
                .index("twitter")
                .type("tweet")
                .id("1")
                .refresh(true)
                .build();
        client.execute(index);
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void explain() {
        try {
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
        } catch (IOException e) {
            fail("Failed during the explain  with valid parameters. Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue((Boolean) result.getValue("ok"));
        assertTrue(result.isSucceeded());
    }

}
