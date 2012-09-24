package io.searchbox.core;

import io.searchbox.Action;
import io.searchbox.client.JestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

//@RunWith(ElasticsearchRunner.class)
//@ElasticsearchNode
public class ExplainIntegrationTest extends AbstractIntegrationTest {

	final static Logger log = LoggerFactory.getLogger(ExplainIntegrationTest.class);

    //@Test
    public void explain() {
        try {
            executeTestCase(new Explain.Builder("\"term\" : { \"message\" : \"search\" }").index("twitter").type("tweet").id("1").build());
            log.info("Successfully finished explain operation");
        } catch (IOException e) {
            fail("Failed during the explain  with valid parameters. Exception:" + e.getMessage());
        }
    }

    //@Test
    public void explainWithDefaultIndex() {
        try {
            executeTestCase(new Explain.Builder("\"term\" : { \"message\" : \"search\" }").type("tweet").id("1").build());

            log.info("Successfully finished explain operation");
        } catch (IOException e) {
            fail("Failed during the explain with valid parameters. Exception:" + e.getMessage());
        }
    }

    //@Test
    public void explainWithDefaultIndexAndType() {
        try {
            executeTestCase(new Explain.Builder("\"term\" : { \"message\" : \"search\" }").id("1").build());

            log.info("Successfully finished explain operation");
        } catch (IOException e) {
            fail("Failed during the explain with valid parameters. Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue((Boolean) result.getValue("ok"));
        assertTrue(result.isSucceeded());
    }

}
