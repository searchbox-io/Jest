package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class BulkIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void bulkOperationWithIndex() {
        try {
            Bulk bulk = new Bulk();
            Map<String, String> source = new HashMap<String, String>();
            source.put("user", "kimchy");
            bulk.addIndex(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithIndexJsonSource() {
        try {
            Bulk bulk = new Bulk();
            String source = "{\"user\":\"super\"}";
            bulk.addIndex(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithSingleDelete() {
        try {
            Bulk bulk = new Bulk();
            bulk.addDelete(new Delete.Builder("1").index("twitter").type("tweet").build());
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithMultipleIndex() {
        try {
            Bulk bulk = new Bulk();
            Map<String, String> source = new HashMap<String, String>();
            source.put("user", "kimcy");
            bulk.addIndex(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
            bulk.addIndex(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build());
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithMultipleDelete() {
        try {
            Bulk bulk = new Bulk();
            bulk.addDelete(new Delete.Builder("1").index("twitter").type("tweet").build());
            bulk.addDelete(new Delete.Builder("2").index("twitter").type("tweet").build());
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithMultipleIndexAndDelete() {
        try {
            Bulk bulk = new Bulk();
            Map<String, String> source = new HashMap<String, String>();
            source.put("field", "value");
            bulk.addIndex(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
            bulk.addIndex(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build());
            bulk.addDelete(new Delete.Builder("1").index("twitter").type("tweet").build());
            bulk.addDelete(new Delete.Builder("2").index("twitter").type("tweet").build());
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithSourceList() {
        try {
            Bulk bulk = new Bulk("twitter", "tweet");
            TestArticleModel model1 = new TestArticleModel("tweet1");
            TestArticleModel model2 = new TestArticleModel("2", "tweet2");
            List<TestArticleModel> modelList = Arrays.asList(model1, model2);
            bulk.addIndexList(modelList);
            executeTestCase(bulk);
        } catch (Exception e) {
            fail("Failed during bulk operation Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        ((List) result.getValue("items")).get(0);
        if ((Map) ((Map) ((Map) ((Map) ((List) result.getValue("items")).get(0)))).get("index") != null) {
            assertTrue((Boolean) ((Map) ((Map) ((Map) ((Map) ((List) result.getValue("items")).get(0)))).get("index")).get("ok"));
        }
        if ((Map) ((Map) ((Map) ((Map) ((List) result.getValue("items")).get(0)))).get("delete") != null) {
            assertTrue((Boolean) ((Map) ((Map) ((Map) ((Map) ((List) result.getValue("items")).get(0)))).get("delete")).get("ok"));
        }
        assertTrue(result.isSucceeded());
    }
}
