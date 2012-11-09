package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
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
            executeTestCase(new MultiGet(docs));
        } catch (Exception e) {
            fail("Failed during the multi get valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void getDocumentWithMultipleIds() {
        try {
            executeTestCase(new MultiGet(new String[]{"1", "2", "3"}));
        } catch (Exception e) {
            fail("Failed during the multi get valid parameters. Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        assertFalse(result.isSucceeded());
    }
}
