package io.searchbox.core;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class BulkTest {

    @Test
    public void emptyBulkOperation(){
        executeAsserts(new Bulk());
    }


    @Test
    public void bulkOperationWithIndex(){
        Bulk bulk = new Bulk();
        Map source = new HashMap();
        source.put("field","value");
        bulk.addIndex(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
        executeAsserts(bulk);
        String expectedData = "{ \"index\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"1\"}}\n" +
                "{\"field\":\"value\"}";
        assertEquals(expectedData.trim(), bulk.getData().toString().trim());
    }


    @Test
    public void bulkOperationWithSingleDelete(){
        Bulk bulk = new Bulk();
        bulk.addDelete(new Delete.Builder("twitter","tweet").id("1").build());
        executeAsserts(bulk);
        String expectedData = "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"1\"}}\n";
        assertEquals(expectedData.trim(), bulk.getData().toString().trim());
    }


    @Test
    public void bulkOperationWithMultipleIndex(){
        Bulk bulk = new Bulk();
        Map source = new HashMap();
        source.put("field","value");
        bulk.addIndex(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
        bulk.addIndex(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build());
        executeAsserts(bulk);
        String expectedData = "{ \"index\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"1\"}}\n" +
                "{\"field\":\"value\"}\n" +
                "{ \"index\" : { \"_index\" : \"elasticsearch\", \"_type\" : \"jest\", \"_id\" : \"2\"}}\n" +
                "{\"field\":\"value\"}";
        assertEquals(expectedData.trim(), bulk.getData().toString().trim());
    }

    @Test
    public void bulkOperationWithMultipleDelete(){
        Bulk bulk = new Bulk();
        bulk.addDelete(new Delete.Builder("twitter","tweet").id("1").build());
        bulk.addDelete(new Delete.Builder("twitter","tweet").id("2").build());
        executeAsserts(bulk);
        String expectedData = "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"1\"}}\n"+
                "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"2\"}}";
        assertEquals(expectedData.trim(), bulk.getData().toString().trim());
    }


    @Test
    public void bulkOperationWithMultipleIndexAndDelete(){
        Bulk bulk = new Bulk();
        Map source = new HashMap();
        source.put("field","value");
        bulk.addIndex(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
        bulk.addIndex(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build());
        bulk.addDelete(new Delete.Builder("twitter","tweet").id("1").build());
        bulk.addDelete(new Delete.Builder("twitter","tweet").id("2").build());
        executeAsserts(bulk);
        String expectedData = "{ \"index\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"1\"}}\n" +
                "{\"field\":\"value\"}\n" +
                "{ \"index\" : { \"_index\" : \"elasticsearch\", \"_type\" : \"jest\", \"_id\" : \"2\"}}\n" +
                "{\"field\":\"value\"}\n"+
                "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"1\"}}\n"+
                "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"2\"}}";
        assertEquals(expectedData.trim(), bulk.getData().toString().trim());
    }



    private void executeAsserts(Bulk bulk) {
        assertEquals("POST", bulk.getRestMethodName());
        assertEquals("BULK",bulk.getName());
        assertEquals("/_bulk",bulk.getURI());
    }
}
