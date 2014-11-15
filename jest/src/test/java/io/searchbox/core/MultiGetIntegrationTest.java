package io.searchbox.core;

import com.google.gson.JsonArray;
import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numDataNodes = 1)
public class MultiGetIntegrationTest extends AbstractIntegrationTest {

    private static final String TEST_INDEX = "twitter";
    private static final String TEST_TYPE = "tweet";

    @Before
    public void setup() throws IOException {
        client().index(new IndexRequest(TEST_INDEX, TEST_TYPE, "1").source("{\"text\":\"pumpkin\"}")).actionGet();
        client().index(new IndexRequest(TEST_INDEX, TEST_TYPE, "2").source("{\"text\":\"spice\"}")).actionGet();
        client().index(new IndexRequest(TEST_INDEX, TEST_TYPE, "3").source("{\"text\":\"latte\"}")).actionGet();
    }

    @Test
    public void getMultipleDocsWhenAllIndexedDocsAreRequested() throws IOException {
        Doc doc1 = new Doc(TEST_INDEX, TEST_TYPE, "1");
        Doc doc2 = new Doc(TEST_INDEX, TEST_TYPE, "2");
        Doc doc3 = new Doc(TEST_INDEX, TEST_TYPE, "3");
        List<Doc> docs = Arrays.asList(doc1, doc2, doc3);

        Action action = new MultiGet.Builder.ByDoc(docs).build();
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        result.getJsonObject().getAsJsonArray("docs");

        JsonArray actualDocs = result.getJsonObject().getAsJsonArray("docs");
        assertEquals("Number of docs in response should match the number of docs in requests.", 3, actualDocs.size());

        assertTrue("Document 1 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(0).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
        assertTrue("Document 2 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(1).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
        assertTrue("Document 3 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(2).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
    }

    @Test
    public void getMultipleDocsWhenSomeIndexedDocsAreRequested() throws IOException {
        Doc doc1 = new Doc(TEST_INDEX, TEST_TYPE, "1");
        Doc doc3 = new Doc(TEST_INDEX, TEST_TYPE, "3");
        List<Doc> docs = Arrays.asList(doc1, doc3);

        Action action = new MultiGet.Builder.ByDoc(docs).build();
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonArray actualDocs = result.getJsonObject().getAsJsonArray("docs");
        assertEquals("Number of docs in response should match the number of docs in requests.", 2, actualDocs.size());

        assertTrue("Document 1 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(0).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
        assertTrue("Document 3 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(1).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
    }

    @Test
    public void getMultipleDocsWhenNonIndexedDocsAreRequested() throws IOException {
        Doc doc1 = new Doc(TEST_INDEX, TEST_TYPE, "1");
        Doc doc3 = new Doc(TEST_INDEX, TEST_TYPE, "3");
        Doc doc6 = new Doc(TEST_INDEX, TEST_TYPE, "6");
        List<Doc> docs = Arrays.asList(doc1, doc3, doc6);

        Action action = new MultiGet.Builder.ByDoc(docs).build();
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonArray actualDocs = result.getJsonObject().getAsJsonArray("docs");
        assertEquals("Number of docs in response should match the number of docs in requests.", 3, actualDocs.size());

        assertTrue("Document 1 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(0).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
        assertTrue("Document 2 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(1).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
        assertFalse("Document 6 is not indexed and should have not been found by the MultiGet request.",
                actualDocs.get(2).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
    }

    @Test
    public void getDocumentWithMultipleIdsWhenAllIndexedDocsAreRequested() throws IOException {
        Action action = new MultiGet.Builder.ById(TEST_INDEX, TEST_TYPE).addId("1").addId("2").addId("3").build();
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonArray actualDocs = result.getJsonObject().getAsJsonArray("docs");
        assertEquals("Number of docs in response should match the number of docs in requests.", 3, actualDocs.size());

        assertTrue("Document 1 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(0).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
        assertTrue("Document 2 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(1).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
        assertTrue("Document 3 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(2).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
    }

    @Test
    public void getDocumentWithMultipleIdsWhenSomeIndexedDocsAreRequested() throws IOException {
        Action action = new MultiGet.Builder.ById(TEST_INDEX, TEST_TYPE).addId("1").addId("3").build();
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonArray actualDocs = result.getJsonObject().getAsJsonArray("docs");
        assertEquals("Number of docs in response should match the number of docs in requests.", 2, actualDocs.size());

        assertTrue("Document 1 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(0).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
        assertTrue("Document 3 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(1).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
    }

    @Test
    public void getDocumentWithMultipleIdsWhenNonIndexedDocsAreRequested() throws IOException {
        Action action = new MultiGet.Builder.ById(TEST_INDEX, TEST_TYPE).addId("1").addId("3").addId("7").build();
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonArray actualDocs = result.getJsonObject().getAsJsonArray("docs");
        assertEquals("Number of docs in response should match the number of docs in requests.", 3, actualDocs.size());

        assertTrue("Document 1 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(0).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
        assertTrue("Document 3 is indexed and should have been found by the MultiGet request.",
                actualDocs.get(1).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
        assertFalse("Document 7 is not indexed and should not have been found by the MultiGet request.",
                actualDocs.get(2).getAsJsonObject().getAsJsonPrimitive("found").getAsBoolean());
    }

}
