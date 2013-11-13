package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.searchbox.Action;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public void bulkOperationWithCustomGson() throws Exception {
        Date date = new Date(1356998400000l); // Tue, 01 Jan 2013 00:00:00 GMT
        String dateStyle = "yyyy-**-MM";

        JestClientFactory factory = new JestClientFactory();
        HttpClientConfig httpClientConfig = new HttpClientConfig.
                Builder("http://localhost:" + getPort())
                .multiThreaded(true)
                .gson(new GsonBuilder().setDateFormat(dateStyle).create())
                .build();
        factory.setHttpClientConfig(httpClientConfig);
        JestHttpClient client = (JestHttpClient) factory.getObject();

        Map<String, Object> source = new HashMap<String, Object>();
        source.put("user", date);
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                .build();

        JestResult result = client.execute(bulk);
        assertNotNull(result);
        ((List) result.getValue("items")).get(0);
        if (((Map) ((List) result.getValue("items")).get(0)).get("index") != null) {
            assertTrue((Boolean) ((Map) ((Map) ((List) result.getValue("items")).get(0)).get("index")).get("ok"));
        }
        if (((Map) ((List) result.getValue("items")).get(0)).get("delete") != null) {
            assertTrue((Boolean) ((Map) ((Map) ((List) result.getValue("items")).get(0)).get("delete")).get("ok"));
        }
        assertTrue(result.isSucceeded());

        GetResponse getResponse = directClient.get(new GetRequest("twitter", "tweet", "1")).actionGet(5000);
        assertNotNull(getResponse);
        // use date formatter to avoid timezone issues when testing
        SimpleDateFormat df = new SimpleDateFormat(dateStyle);
        assertEquals(df.format(date), getResponse.getSourceAsMap().get("user"));
    }

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
    public void bulkOperationWithDefaultIndexAndDefaultType() {
        try {
            Map<String, String> source1 = new HashMap<String, String>();
            source1.put("user name", "kimchy olga john doe");

            Bulk bulk = new Bulk.Builder()
                    .defaultIndex("twitter")
                    .defaultType("tweet")
                    .addAction(new Index.Builder(source1).id("1").build())
                    .build();
            executeTestCase(bulk);

            GetResponse getResponse = directClient.get(new GetRequest("twitter", "tweet", "1")).actionGet();
            assertNotNull(getResponse);
            assertEquals(new Gson().toJson(source1), getResponse.getSourceAsString());
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithIndexWithSourceIncludingWhitespace() {
        try {
            Map<String, String> source1 = new HashMap<String, String>();
            source1.put("user name", "kimchy olga john doe");

            String source2 = "{\"k e y\"    :   \" val v a l \"    }";

            Bulk bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(source1).index("twitter").type("tweet").id("1").build())
                    .addAction(new Index.Builder(source2).index("twitter").type("tweet").id("2").build())
                    .build();
            executeTestCase(bulk);

            GetResponse getResponse = directClient.get(new GetRequest("twitter", "tweet", "1")).actionGet();
            assertNotNull(getResponse);
            assertEquals(new Gson().toJson(source1), getResponse.getSourceAsString());

            getResponse = directClient.get(new GetRequest("twitter", "tweet", "2")).actionGet();
            assertNotNull(getResponse);
            assertEquals(source2, getResponse.getSourceAsString());
        } catch (IOException e) {
            fail("Failed during the bulk operation Exception:" + e.getMessage());
        }
    }

    @Test
    public void bulkOperationWithIndexWithSourceIncludingLineBreak() {
        try {
            Map<String, String> source1 = new HashMap<String, String>();
            source1.put("user name", "kimchy\nolga\njohn doe");

            String source2 = "{\"k e y\"    :   \" val\nv a\r\nl \"    }";

            Bulk bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(source1).index("twitter").type("tweet").id("1").build())
                    .addAction(new Index.Builder(source2).index("twitter").type("tweet").id("2").build())
                    .build();

            JestResult result = client.execute(bulk);
            assertNotNull(result);
            assertFalse(result.isSucceeded());
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
            String script = "{" +
                    "    \"script\" : \"ctx._source.user += tag\"," +
                    "    \"params\" : {" +
                    "        \"tag\" : \"_osman\"" +
                    "    }" +
                    "}";

            Map<String, String> source = new HashMap<String, String>();
            source.put("user", "kimchy");
            Bulk bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                    .addAction(new Update.Builder(StringUtils.chomp(script)).index("twitter").type("tweet").id("1").build())
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
                    .addAction(new Delete.Builder("1").index("twitter").type("tweet").build())
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
                    .addAction(new Delete.Builder("1").index("twitter").type("tweet").build())
                    .addAction(new Delete.Builder("2").index("twitter").type("tweet").build())
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
                    .addAction(new Delete.Builder("1").index("twitter").type("tweet").build())
                    .addAction(new Delete.Builder("2").index("twitter").type("tweet").build())
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
