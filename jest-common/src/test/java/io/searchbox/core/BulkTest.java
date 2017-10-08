package io.searchbox.core;

import com.google.gson.Gson;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */
public class BulkTest {

    @Test
    public void emptyBulkOperation() {
        executeAsserts(new Bulk.Builder().build());
    }

    @Test
    public void bulkOperationWithIndex() {
        Map source = new HashMap();
        source.put("field", "value");

        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                .build();
        executeAsserts(bulk);
        String expectedData = "{\"index\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n" +
                "{\"field\":\"value\"}";
        assertEquals(expectedData.trim(), bulk.getData(new Gson()).toString().trim());
    }

    @Test
    public void bulkOperationWithSingleDelete() {
        Bulk bulk = new Bulk.Builder()
                .addAction(new Delete.Builder("1").index("twitter").type("tweet").build())
                .build();

        executeAsserts(bulk);
        String expectedData = "{\"delete\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n";
        assertEquals(expectedData.trim(), bulk.getData(new Gson()).toString().trim());
    }

    @Test
    public void bulkOperationWithMultipleIndex() {
        Map source = new HashMap();
        source.put("field", "value");

        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                .addAction(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build())
                .build();

        executeAsserts(bulk);
        String expectedData = "{\"index\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n" +
                "{\"field\":\"value\"}\n" +
                "{\"index\":{\"_id\":\"2\",\"_index\":\"elasticsearch\",\"_type\":\"jest\"}}\n" +
                "{\"field\":\"value\"}";
        assertEquals(expectedData.trim(), bulk.getData(new Gson()).toString().trim());
    }

    @Test
    public void bulkOperationWithMultipleDelete() {
        Bulk bulk = new Bulk.Builder()
                .addAction(new Delete.Builder("1").index("twitter").type("tweet").build())
                .addAction(new Delete.Builder("2").index("twitter").type("tweet").build())
                .build();

        executeAsserts(bulk);
        String expectedData = "{\"delete\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n" +
                "{\"delete\":{\"_id\":\"2\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}";
        assertEquals(expectedData.trim(), bulk.getData(new Gson()).toString().trim());
    }

    @Test
    public void bulkOperationWithMultipleIndexAndDelete() {
        Map source = new HashMap();
        source.put("field", "value");

        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                .addAction(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build())
                .addAction(new Delete.Builder("1").index("twitter").type("tweet").build())
                .addAction(new Delete.Builder("2").index("twitter").type("tweet").build())
                .build();

        executeAsserts(bulk);
        String expectedData = "{\"index\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n" +
                "{\"field\":\"value\"}\n" +
                "{\"index\":{\"_id\":\"2\",\"_index\":\"elasticsearch\",\"_type\":\"jest\"}}\n" +
                "{\"field\":\"value\"}\n" +
                "{\"delete\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n" +
                "{\"delete\":{\"_id\":\"2\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}";
        assertEquals(expectedData.trim(), bulk.getData(new Gson()).trim());
    }

    @Test
    public void testUris() {
        Bulk bulkWitIndex = new Bulk.Builder().defaultIndex("twitter").build();
        assertEquals("twitter/_bulk", bulkWitIndex.getURI());

        Bulk bulkWitIndexAndType = new Bulk.Builder().defaultIndex("twitter").defaultType("tweet").build();
        assertEquals("twitter/tweet/_bulk", bulkWitIndexAndType.getURI());
    }

    private void executeAsserts(Bulk bulk) {
        assertEquals("POST", bulk.getRestMethodName());
        assertEquals("/_bulk", bulk.getURI());
    }
}
