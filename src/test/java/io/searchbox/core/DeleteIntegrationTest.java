package io.searchbox.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Parameters;
import io.searchbox.client.JestResult;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class DeleteIntegrationTest extends AbstractIntegrationTest {

    private static Logger log = Logger.getLogger(DeleteIntegrationTest.class.getName());

    @Test
    public void deleteDocument() {
        try {
            executeTestCase(new Delete.Builder("1").index("twitter").type("tweet").build());
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void deleteDocumentFromDefaultIndex() {
        client.registerDefaultIndex("twitter");
        try {
            executeTestCase(new Delete.Builder("1").type("tweet").build());
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void deleteDocumentFromDefaultIndexAndType() {
        client.registerDefaultIndex("twitter");
        client.registerDefaultType("tweet");
        try {
            executeTestCase(new Delete.Builder("1").build());
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }


    @Test
    @ElasticsearchIndex(indexName = "cvbank")
    public void deleteRealDocument() {
        try {
            Index index = new Index.Builder("{\"user\":\"kimchy\"}").index("cvbank").type("candidate").id("1").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);
            JestResult result = client.execute(new Delete.Builder("1").index("cvbank").type("candidate").build());
            assertNotNull(result);
            assertTrue((Boolean) result.getValue("ok"));
            assertTrue(result.isSucceeded());
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(Delete delete) throws RuntimeException, IOException {
        JestResult result = client.execute(delete);
        assertNotNull(result);
        assertTrue((Boolean) result.getValue("ok"));
        assertFalse(result.isSucceeded());
    }


}
