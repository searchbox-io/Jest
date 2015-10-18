package io.searchbox.core;

import io.searchbox.client.JestResultHandler;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author Dogukan Sonmez
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class DeleteIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void deleteNonExistingDocument() throws IOException {
        DocumentResult result = client.execute(new Delete.Builder("1")
                .index("twitter")
                .type("tweet")
                .build());
        assertFalse(result.isSucceeded());
        assertEquals("twitter", result.getIndex());
        assertEquals("tweet", result.getType());
        assertEquals("1", result.getId());
    }

    @Ignore // async execution disturbs flow of the test suite
    @Test
    public void deleteDocumentAsynchronously() throws InterruptedException, ExecutionException, IOException {
        client.executeAsync(new Delete.Builder("1")
                .index("twitter")
                .type("tweet")
                .build(), new JestResultHandler<DocumentResult>() {
            @Override
            public void completed(DocumentResult result) {
                assertFalse(result.isSucceeded());
            }

            @Override
            public void failed(Exception ex) {
                fail("failed during the asynchronous calling");
            }
        });
    }

    @Test
    public void deleteRealDocument() throws IOException {
        Index index = new Index.Builder("{\"user\":\"kimchy\"}").index("cvbank").type("candidate").id("1").refresh(true).build();
        client.execute(index);
        DocumentResult result = client.execute(new Delete.Builder("1")
                .index("cvbank")
                .type("candidate")
                .build());

        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals("cvbank", result.getIndex());
        assertEquals("candidate", result.getType());
        assertEquals("1", result.getId());
    }

}
