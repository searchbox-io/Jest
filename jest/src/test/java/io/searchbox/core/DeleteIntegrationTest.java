package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author Dogukan Sonmez
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class DeleteIntegrationTest extends AbstractIntegrationTest {

    public static final String INDEX = "twitter";
    public static final String TYPE = "tweet";
    public static final String ID = "1";

    @Before
    public void createIndex() throws IOException {
        CreateIndex createIndex = new CreateIndex.Builder(INDEX).build();

        JestResult result = client.execute(createIndex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void deleteNonExistingDocument() throws IOException {
        DocumentResult result = client.execute(new Delete.Builder(ID)
                .index(INDEX)
                .type(TYPE)
                .id(ID)
                .build());
        assertFalse(result.isSucceeded());
        assertEquals(INDEX, result.getIndex());
        assertEquals("tweet", result.getType());
        assertEquals(ID, result.getId());
    }

    @Test
    public void deleteDocumentAsynchronously() throws InterruptedException, ExecutionException, IOException {
        client.executeAsync(new Delete.Builder(ID)
                .index(INDEX)
                .type(TYPE)
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

        Thread.sleep(500);
    }

    @Test
    public void deleteRealDocument() throws IOException {
        Index index = new Index.Builder("{\"user\":\"kimchy\"}").index("cvbank").type("candidate").id(ID).refresh(true).build();
        client.execute(index);
        DocumentResult result = client.execute(new Delete.Builder(ID)
                .index("cvbank")
                .type("candidate")
                .build());

        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals("cvbank", result.getIndex());
        assertEquals("candidate", result.getType());
        assertEquals(ID, result.getId());
    }

    @After
    public void deleteIndex() throws IOException {
        DeleteIndex indicesExists = new DeleteIndex.Builder(INDEX).build();
        JestResult result = client.execute(indicesExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

}
