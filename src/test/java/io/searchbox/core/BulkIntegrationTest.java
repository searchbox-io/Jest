package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
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

    @ElasticsearchClient
    Client directClient;

    @Test
    public void bulkOperationWithIndex() {
        try {
            Map<String, String> source = new HashMap<String, String>();
            source.put("user", "kimchy");
            Bulk bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                    .build();
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithIndexWithParam() {
        try {
            Map<String, String> source = new HashMap<String, String>();
            source.put("user", "kimchy");
            Bulk bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(source)
                            .index("twitter")
                            .type("tweet")
                            .id("1")
                            .setParameter(Parameters.VERSION, 6)
                            .build())
                    .build();

            // should fail because version 6 does not exist yet
            JestResult result = client.execute(bulk);
            assertNotNull(result);
            assertTrue(result.getJsonString().contains("VersionConflictEngineException"));
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithIndexAndUpdate() {
        try {
            String script = "{\n" +
                    "    \"script\" : \"ctx._source.user += tag\",\n" +
                    "    \"params\" : {\n" +
                    "        \"tag\" : \"_osman\"\n" +
                    "    }\n" +
                    "}";

            Map<String, String> source = new HashMap<String, String>();
            source.put("user", "kimchy");
            Bulk bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                    .addAction(new Update.Builder(script).index("twitter").type("tweet").id("1").build())
                    .build();
            executeTestCase(bulk);

            GetResponse getResponse = directClient.get(new GetRequest("twitter", "tweet", "1")).actionGet();
            assertNotNull(getResponse);
            assertEquals("{\"user\":\"kimchy_osman\"}", getResponse.getSourceAsString());
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithIndexJsonSource() {
        try {
            String source = "{\"user\":\"super\"}";
            Bulk bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                    .build();
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithSingleDelete() {
        try {
            Bulk bulk = new Bulk.Builder()
                    .addAction(new Delete.Builder().id("1").index("twitter").type("tweet").build())
                    .build();
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithMultipleIndex() {
        try {
            Map<String, String> source = new HashMap<String, String>();
            source.put("user", "kimcy");
            Bulk bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                    .addAction(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build())
                    .build();
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithIndexCreateOpType() {
        try {
            Map<String, String> source = new HashMap<String, String>();
            source.put("user", "kimcy");
            Bulk bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                    .addAction(new Index.Builder(source)
                            .index("twitter").type("tweet").id("1").setParameter(Parameters.OP_TYPE, "create").build())
                    .build();
            JestResult result = executeTestCase(bulk);

            // second index request with create op type should fail because it's a duplicate of the first index request
            assertNotNull(
                    result.getJsonObject().getAsJsonArray("items").get(1).getAsJsonObject().getAsJsonObject("create").get("error").getAsString()
            );
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithMultipleDelete() {
        try {
            Bulk bulk = new Bulk.Builder()
                    .addAction(new Delete.Builder().id("1").index("twitter").type("tweet").build())
                    .addAction(new Delete.Builder().id("2").index("twitter").type("tweet").build())
                    .build();
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithMultipleIndexAndDelete() {
        try {
            Map<String, String> source = new HashMap<String, String>();
            source.put("field", "value");
            Bulk bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                    .addAction(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build())
                    .addAction(new Delete.Builder().id("1").index("twitter").type("tweet").build())
                    .addAction(new Delete.Builder().id("2").index("twitter").type("tweet").build())
                    .build();
            executeTestCase(bulk);
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithSourceList() {
        try {
            TestArticleModel model1 = new TestArticleModel("tweet1");
            TestArticleModel model2 = new TestArticleModel("2", "tweet2");
            List<Index> modelList = Arrays.asList(
                    new Index.Builder(model1).build(),
                    new Index.Builder(model2).build()
            );

            Bulk bulk = new Bulk.Builder()
                    .defaultIndex("twitter")
                    .defaultType("tweet")
                    .addAction(modelList)
                    .build();
            executeTestCase(bulk);
        } catch (Exception e) {
            fail("Failed during bulk operation Exception:" + e.getMessage());
        }
    }

    private JestResult executeTestCase(Action action) throws RuntimeException, IOException {
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
        return result;
    }
}
