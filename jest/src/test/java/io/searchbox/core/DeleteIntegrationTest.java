package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class DeleteIntegrationTest extends AbstractIntegrationTest {

    private final static Logger log = LoggerFactory.getLogger(DeleteIntegrationTest.class);

    @Test
    public void deleteDocument() {
        try {
            JestResult result = client.execute(new Delete.Builder( "1")
                    .index("twitter")
                    .type("tweet")
                    .build());
            executeTestCase(result);
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void deleteDocumentAsynchronously() {
        try {

            client.executeAsync(new Delete.Builder( "1")
                    .index("twitter")
                    .type("tweet")
                    .build(), new JestResultHandler<JestResult>() {
                @Override
                public void completed(JestResult result) {
                    executeTestCase(result);
                }

                @Override
                public void failed(Exception ex) {
                    fail("failed during the asynchronous calling");
                }
            });
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "cvbank")
    public void deleteRealDocument() {
        try {
            Index index = new Index.Builder("{\"user\":\"kimchy\"}").index("cvbank").type("candidate").id("1").refresh(true).build();
            client.execute(index);
            JestResult result = client.execute(new Delete.Builder("1")
                    .index("cvbank")
                    .type("candidate")
                    .build());

            assertNotNull(result);
            assertTrue((Boolean) result.getValue("ok"));
            assertTrue(result.isSucceeded());
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(JestResult result) {
        assertNotNull(result);
        assertTrue((Boolean) result.getValue("ok"));
        assertFalse(result.isSucceeded());
    }


}
