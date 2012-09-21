package io.searchbox.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.ElasticSearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class BulkIntegrationTest extends AbstractIntegrationTest{

    @Test
    public void bulkOperationWithIndex(){
        try {
            Bulk bulk = new Bulk();
            Map<String, String> source = new HashMap<String, String>();
            source.put("user","kimchy");
            bulk.addIndex(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithSingleDelete(){
        try {
            Bulk bulk = new Bulk();
            bulk.addDelete(new Delete.Builder("1").index("twitter").type("tweet").build());
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithMultipleIndex(){
        try {
            Bulk bulk = new Bulk();
            Map<String, String> source = new HashMap<String, String>();
            source.put("user","kimcy");
            bulk.addIndex(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
            bulk.addIndex(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build());
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithMultipleDelete(){
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
    public void bulkOperationWithMultipleIndexAndDelete(){
        try {
            Bulk bulk = new Bulk();
            Map<String, String> source = new HashMap<String, String>();
            source.put("field","value");
            bulk.addIndex(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
            bulk.addIndex(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build());
            bulk.addDelete(new Delete.Builder("1").index("twitter").type("tweet").build());
            bulk.addDelete(new Delete.Builder("2").index("twitter").type("tweet").build());
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        ElasticSearchResult result = client.execute(action);
        assertNotNull(result);
        ((List)result.getValue("items")).get(0);
        //assertTrue((Boolean) ((Map)((Map)((Map)((Map)((List)result.getValue("items")).get(0)))).get("index")).get("ok"));
        assertTrue(result.isSucceeded());
    }

}
