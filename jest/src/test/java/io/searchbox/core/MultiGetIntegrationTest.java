package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class MultiGetIntegrationTest extends AbstractIntegrationTest {

    @Before
    public void before() throws IOException {
        CreateIndex createIndex = new CreateIndex.Builder("twitter").build();
        client.execute(createIndex);
        Index index1 = new Index.Builder("{\"text\":\"awesome\"}").index("twitter").type("tweet").id("1").build();
        Index index2 = new Index.Builder("{\"text\":\"awesome\"}").index("twitter").type("tweet").id("2").build();
        Index index3 = new Index.Builder("{\"text\":\"awesome\"}").index("twitter").type("tweet").id("3").build();
        client.execute(index1);
        client.execute(index2);
        client.execute(index3);
    }

    @After
    public void after() throws IOException {
        DeleteIndex deleteIndex = new DeleteIndex.Builder("twitter").build();
        client.execute(deleteIndex);
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
