package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 1)
public class BulkIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void bulkOperationWithCustomGson() throws Exception {
        Date date = new Date(1356998400000l); // Tue, 01 Jan 2013 00:00:00 GMT
        String dateStyle = "yyyy-**-MM";

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
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        ((List) result.getValue("items")).get(0);
        if (((Map) ((List) result.getValue("items")).get(0)).get("index") != null) {
            assertEquals("twitter", ((Map) ((Map) ((List) result.getValue("items")).get(0)).get("index")).get("_index"));
        }
        if (((Map) ((List) result.getValue("items")).get(0)).get("delete") != null) {
            assertTrue((Boolean) ((Map) ((Map) ((List) result.getValue("items")).get(0)).get("delete")).get("ok"));
        }

        GetResponse getResponse = client().get(new GetRequest("twitter", "tweet", "1")).actionGet(5000);
        assertNotNull(getResponse);
        // use date formatter to avoid timezone issues when testing
        SimpleDateFormat df = new SimpleDateFormat(dateStyle);
        assertEquals(df.format(date), getResponse.getSourceAsMap().get("user"));
    }

    @Test
    public void bulkOperationWithIndex() throws IOException {
        Map<String, String> source = new HashMap<String, String>();
        source.put("user", "kimchy");
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                .build();
        executeTestCase(bulk);
    }

    @Test
    public void bulkOperationWithDefaultIndexAndDefaultType() throws IOException {
        Map<String, String> source1 = new HashMap<String, String>();
        source1.put("user name", "kimchy olga john doe");

        Bulk bulk = new Bulk.Builder()
                .defaultIndex("twitter")
                .defaultType("tweet")
                .addAction(new Index.Builder(source1).id("1").build())
                .build();
        executeTestCase(bulk);

        GetResponse getResponse = client().get(new GetRequest("twitter", "tweet", "1")).actionGet();
        assertNotNull(getResponse);
        assertEquals(new Gson().toJson(source1), getResponse.getSourceAsString());
    }

    @Test
    public void bulkOperationWithIndexWithSourceIncludingWhitespace() throws IOException {
        Map<String, String> source1 = new HashMap<String, String>();
        source1.put("user name", "kimchy olga john doe");

        String source2 = "{\"k e y\"    :   \" val v a l \"    }";

        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source1).index("twitter").type("tweet").id("1").build())
                .addAction(new Index.Builder(source2).index("twitter").type("tweet").id("2").build())
                .build();
        executeTestCase(bulk);

        GetResponse getResponse = client().get(new GetRequest("twitter", "tweet", "1")).actionGet();
        assertNotNull(getResponse);
        assertEquals(new Gson().toJson(source1), getResponse.getSourceAsString());

        getResponse = client().get(new GetRequest("twitter", "tweet", "2")).actionGet();
        assertNotNull(getResponse);
        assertEquals(source2, getResponse.getSourceAsString());
    }

    @Test
    public void bulkOperationWithIndexWithSourceIncludingLineBreak() throws IOException {
        Map<String, String> source1 = new HashMap<String, String>();
        source1.put("user name", "kimchy\nolga\njohn doe");

        String source2 = "{\"k e y\"    :   \" val\nv a\r\nl \"    }";

        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source1).index("twitter").type("tweet").id("1").build())
                .addAction(new Index.Builder(source2).index("twitter").type("tweet").id("2").build())
                .build();

        JestResult result = client.execute(bulk);
        assertFalse(result.isSucceeded());
    }

    @Test
    public void bulkOperationWithIndexWithParam() throws IOException {
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
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void bulkOperationWithIndexAndUpdate() throws IOException {
        String script = "{" +
                "    \"lang\" : \"groovy\"," +
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

        GetResponse getResponse = client().get(new GetRequest("twitter", "tweet", "1")).actionGet();
        assertNotNull(getResponse);
        assertEquals("{\"user\":\"kimchy_osman\"}", getResponse.getSourceAsString());
    }

    @Test
    public void bulkOperationWithIndexJsonSource() throws IOException {
        String source = "{\"user\":\"super\"}";
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                .build();
        executeTestCase(bulk);
    }

    @Test
    public void bulkOperationWithSingleDelete() throws IOException {
        Bulk bulk = new Bulk.Builder()
                .addAction(new Delete.Builder("1").index("twitter").type("tweet").build())
                .build();
        executeTestCase(bulk);
    }

    @Test
    public void bulkOperationWithMultipleIndex() throws IOException {
        Map<String, String> source = new HashMap<String, String>();
        source.put("user", "kimcy");
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                .addAction(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build())
                .build();
        executeTestCase(bulk);
    }

    @Test
    public void bulkOperationWithIndexCreateOpType() throws IOException {
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
    }

    @Test
    public void bulkOperationWithMultipleDelete() throws IOException {
        Bulk bulk = new Bulk.Builder()
                .addAction(new Delete.Builder("1").index("twitter").type("tweet").build())
                .addAction(new Delete.Builder("2").index("twitter").type("tweet").build())
                .build();
        executeTestCase(bulk);
    }

    @Test
    public void bulkOperationWithMultipleIndexAndDelete() throws IOException {
        Map<String, String> source = new HashMap<String, String>();
        source.put("field", "value");
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build())
                .addAction(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build())
                .addAction(new Delete.Builder("1").index("twitter").type("tweet").build())
                .addAction(new Delete.Builder("2").index("twitter").type("tweet").build())
                .build();
        executeTestCase(bulk);
    }

    @Test
    public void bulkOperationWithSourceList() throws IOException {
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
    }

    private JestResult executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        ((List) result.getValue("items")).get(0);
        if ((Map) ((Map) ((Map) ((Map) ((List) result.getValue("items")).get(0)))).get("index") != null) {
            assertEquals("twitter", ((Map) ((Map) ((Map) ((Map) ((List) result.getValue("items")).get(0)))).get("index")).get("_index"));
        }
        if ((Map) ((Map) ((Map) ((Map) ((List) result.getValue("items")).get(0)))).get("delete") != null) {
            assertEquals("twitter", ((Map) ((Map) ((Map) ((Map) ((List) result.getValue("items")).get(0)))).get("delete")).get("_index"));
        }
        return result;
    }
}
