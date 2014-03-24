package io.searchbox.core;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class MultiGetIntegrationTest extends AbstractIntegrationTest {

    @Before
    public void setup() throws IOException {
        client().index(new IndexRequest("twitter", "tweet", "1").source("{\"text\":\"awesome\"}")).actionGet();
        client().index(new IndexRequest("twitter", "tweet", "2").source("{\"text\":\"awesome\"}")).actionGet();
        client().index(new IndexRequest("twitter", "tweet", "3").source("{\"text\":\"awesome\"}")).actionGet();
    }

    @Test
    public void getMultipleDocs() {
        Doc doc1 = new Doc("twitter", "tweet", "1");
        Doc doc2 = new Doc("twitter", "tweet", "2");
        Doc doc3 = new Doc("twitter", "tweet", "3");
        List<Doc> docs = new ArrayList<Doc>();
        docs.add(doc1);
        docs.add(doc2);
        docs.add(doc3);
        try {
            executeTestCase(new MultiGet.Builder.ByDoc(docs).build());
        } catch (Exception e) {
            fail("Failed during the multi get valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void getDocumentWithMultipleIds() {
        try {
            executeTestCase(new MultiGet.Builder.ById("twitter", "tweet").addId("1").addId("2").addId("3").build());
        } catch (Exception e) {
            fail("Failed during the multi get valid parameters. Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        assertEquals(3, ((List) result.getJsonMap().get("docs")).size());
    }
}
