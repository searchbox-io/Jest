package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Dogukan Sonmez
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class BulkIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void bulkOperationWithCustomGson() throws Exception {
        String index = "twitter";
        String type = "tweet";
        String id = "1";
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
                .addAction(new Index.Builder(source).index(index).type(type).id(id).build())
                .build();

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        assertEquals(1, items.size());
        assertEquals(0, result.getFailedItems().size());

        BulkResult.BulkResultItem firstItem = items.get(0);
        assertEquals("index", firstItem.operation);
        assertEquals(index, firstItem.index);
        assertEquals(type, firstItem.type);
        assertEquals(id, firstItem.id);
        assertEquals(201, firstItem.status);
        assertNull(firstItem.error);

        GetResponse getResponse = client().get(new GetRequest("twitter", "tweet", "1")).actionGet(5000);
        assertNotNull(getResponse);
        // use date formatter to avoid timezone issues when testing
        SimpleDateFormat df = new SimpleDateFormat(dateStyle);
        assertEquals(df.format(date), getResponse.getSourceAsMap().get("user"));
    }

    @Test
    public void bulkOperationWithIndex() throws IOException {
        String index = "twitter";
        String type = "tweet";
        String id = "1";
        Map<String, String> source = new HashMap<String, String>();
        source.put("user", "kimchy");
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index(index).type(type).id(id).build())
                .build();

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        assertEquals(1, items.size());
        assertEquals(0, result.getFailedItems().size());

        BulkResult.BulkResultItem firstItem = items.get(0);
        assertEquals("index", firstItem.operation);
        assertEquals(index, firstItem.index);
        assertEquals(type, firstItem.type);
        assertEquals(id, firstItem.id);
        assertEquals(201, firstItem.status);
        assertNull(firstItem.error);
    }

    @Test
    public void bulkOperationWithDefaultIndexAndDefaultType() throws IOException {
        String index = "twitter";
        String type = "tweet";
        String id = "1";
        Map<String, String> source1 = new HashMap<String, String>();
        source1.put("user name", "kimchy olga john doe");

        Bulk bulk = new Bulk.Builder()
                .defaultIndex(index)
                .defaultType(type)
                .addAction(new Index.Builder(source1).id(id).build())
                .build();

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        assertEquals(1, items.size());
        assertEquals(0, result.getFailedItems().size());

        BulkResult.BulkResultItem firstItem = items.get(0);
        assertEquals("index", firstItem.operation);
        assertEquals(index, firstItem.index);
        assertEquals(type, firstItem.type);
        assertEquals(id, firstItem.id);
        assertEquals(201, firstItem.status);
        assertNull(firstItem.error);

        GetResponse getResponse = client().get(new GetRequest("twitter", "tweet", "1")).actionGet();
        assertNotNull(getResponse);
        assertEquals(new Gson().toJson(source1), getResponse.getSourceAsString());
    }

    @Test
    public void bulkOperationWithIndexWithSourceIncludingWhitespace() throws IOException {
        String index = "twitter";
        String type = "tweet";
        Map<String, String> source1 = new HashMap<String, String>();
        source1.put("user name", "kimchy olga john doe");

        String source2 = "{\"k e y\"    :   \" val v a l \"    }";

        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source1).index(index).type(type).id("1").build())
                .addAction(new Index.Builder(source2).index(index).type(type).id("2").build())
                .build();

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        assertEquals(2, items.size());
        assertEquals(0, result.getFailedItems().size());

        BulkResult.BulkResultItem firstItem = items.get(0);
        assertEquals("index", firstItem.operation);
        assertEquals(index, firstItem.index);
        assertEquals(type, firstItem.type);
        assertEquals("1", firstItem.id);
        assertEquals(201, firstItem.status);
        assertNull(firstItem.error);

        BulkResult.BulkResultItem secondItem = items.get(1);
        assertEquals("index", secondItem.operation);
        assertEquals(index, secondItem.index);
        assertEquals(type, secondItem.type);
        assertEquals("2", secondItem.id);
        assertEquals(201, secondItem.status);
        assertNull(secondItem.error);

        GetResponse getResponse = client().get(new GetRequest("twitter", "tweet", "1")).actionGet();
        assertNotNull(getResponse);
        assertEquals(new Gson().toJson(source1), getResponse.getSourceAsString());

        getResponse = client().get(new GetRequest("twitter", "tweet", "2")).actionGet();
        assertNotNull(getResponse);
        assertEquals(source2, getResponse.getSourceAsString());
    }

    @Test
    public void bulkOperationWithIndexWithSourceIncludingLineBreak() throws IOException {
        String index = "twitter";
        String type = "tweet";
        Map<String, String> source1 = new HashMap<String, String>();
        source1.put("user name", "kimchy\nolga\njohn doe");

        String source2 = "{\"k e y\"    :   \" val\nv a\r\nl \"    }";

        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source1).index(index).type(type).id("1").build())
                .addAction(new Index.Builder(source2).index(index).type(type).id("2").build())
                .build();

        BulkResult result = client.execute(bulk);
        assertFalse(result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        assertEquals(0, items.size());
        assertEquals(0, result.getFailedItems().size());
    }

    @Test
    public void bulkOperationWithIndexWithParam() throws IOException {
        String index = "twitter";
        String type = "tweet";
        Map<String, String> source = new HashMap<String, String>();
        source.put("user", "kimchy");
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source)
                        .index(index)
                        .type(type)
                        .id("1")
                        .setParameter(Parameters.VERSION, 6)
                        .build())
                .build();

        JestResult result = client.execute(bulk);
        assertFalse(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void bulkOperationWithIndexAndUpdate() throws IOException {
        String index = "twitter";
        String type = "tweet";
        String id = "1";
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
                .addAction(new Index.Builder(source).index(index).type(type).id(id).build())
                .addAction(new Update.Builder(StringUtils.chomp(script)).index(index).type(type).id(id).build())
                .build();

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        assertEquals(2, items.size());
        assertEquals(0, result.getFailedItems().size());

        BulkResult.BulkResultItem firstItem = items.get(0);
        assertEquals("index", firstItem.operation);
        assertEquals(index, firstItem.index);
        assertEquals(type, firstItem.type);
        assertEquals(id, firstItem.id);
        assertEquals(201, firstItem.status);
        assertNull(firstItem.error);

        BulkResult.BulkResultItem secondItem = items.get(1);
        assertEquals("update", secondItem.operation);
        assertEquals(index, secondItem.index);
        assertEquals(type, secondItem.type);
        assertEquals(id, secondItem.id);
        assertEquals(200, secondItem.status);
        assertNull(secondItem.error);

        GetResponse getResponse = client().get(new GetRequest("twitter", "tweet", "1")).actionGet();
        assertNotNull(getResponse);
        assertEquals("{\"user\":\"kimchy_osman\"}", getResponse.getSourceAsString());
    }

    @Test
    public void bulkOperationWithIndexJsonSource() throws IOException {
        String index = "twitter";
        String type = "tweet";
        String id = "1";
        String source = "{\"user\":\"super\"}";
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index(index).type(type).id(id).build())
                .build();

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        assertEquals(1, items.size());
        assertEquals(0, result.getFailedItems().size());

        BulkResult.BulkResultItem firstItem = items.get(0);
        assertEquals("index", firstItem.operation);
        assertEquals(index, firstItem.index);
        assertEquals(type, firstItem.type);
        assertEquals(id, firstItem.id);
        assertEquals(201, firstItem.status);
        assertNull(firstItem.error);
    }

    @Test
    public void bulkOperationWithSingleDelete() throws IOException {
        String index = "twitter";
        String type = "tweet";
        String id = "1";
        Bulk bulk = new Bulk.Builder()
                .addAction(new Delete.Builder(id).index(index).type(type).build())
                .build();

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        assertEquals(1, items.size());
        assertEquals(0, result.getFailedItems().size());

        BulkResult.BulkResultItem firstItem = items.get(0);
        assertEquals("delete", firstItem.operation);
        assertEquals(index, firstItem.index);
        assertEquals(type, firstItem.type);
        assertEquals(id, firstItem.id);
        assertEquals(404, firstItem.status);
        assertNull(firstItem.error);
    }

    @Test
    public void bulkOperationWithMultipleIndex() throws IOException {
        String index1 = "twitter";
        String type1 = "tweet";
        String id1 = "1";
        String index2 = "elasticsearch";
        String type2 = "jest";
        String id2 = "2";

        Map<String, String> source = new HashMap<String, String>();
        source.put("user", "kimcy");
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index(index1).type(type1).id(id1).build())
                .addAction(new Index.Builder(source).index(index2).type(type2).id(id2).build())
                .build();

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        assertEquals(2, items.size());
        assertEquals(0, result.getFailedItems().size());

        BulkResult.BulkResultItem firstItem = items.get(0);
        assertEquals("index", firstItem.operation);
        assertEquals(index1, firstItem.index);
        assertEquals(type1, firstItem.type);
        assertEquals(id1, firstItem.id);
        assertEquals(201, firstItem.status);
        assertNull(firstItem.error);

        BulkResult.BulkResultItem secondItem = items.get(1);
        assertEquals("index", secondItem.operation);
        assertEquals(index2, secondItem.index);
        assertEquals(type2, secondItem.type);
        assertEquals(id2, secondItem.id);
        assertEquals(201, secondItem.status);
        assertNull(secondItem.error);
    }

    @Test
    public void bulkOperationWithIndexCreateOpType() throws IOException {
        String index = "twitter";
        String type = "tweet";
        String id = "1";
        Map<String, String> source = new HashMap<String, String>();
        source.put("user", "kimcy");
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index(index).type(type).id(id).build())
                .addAction(new Index.Builder(source)
                        .index(index).type(type).id(id).setParameter(Parameters.OP_TYPE, "create").build())
                .build();

        BulkResult result = client.execute(bulk);
        assertFalse(result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        List<BulkResult.BulkResultItem> failedItems = result.getFailedItems();
        assertEquals(2, items.size());
        assertEquals(1, failedItems.size());

        BulkResult.BulkResultItem firstItem = items.get(0);
        assertEquals("index", firstItem.operation);
        assertEquals(index, firstItem.index);
        assertEquals(type, firstItem.type);
        assertEquals(id, firstItem.id);
        assertEquals(201, firstItem.status);
        assertNull(firstItem.error);

        BulkResult.BulkResultItem secondItem = items.get(1);
        assertEquals("create", secondItem.operation);
        assertEquals(index, secondItem.index);
        assertEquals(type, secondItem.type);
        assertEquals(id, secondItem.id);
        assertEquals(409, secondItem.status);
        assertNotNull(secondItem.error);
        assertEquals(secondItem, failedItems.get(0));

        // second index request with create op type should fail because it's a duplicate of the first index request
        assertNotNull(
                result.getJsonObject().getAsJsonArray("items").get(1).getAsJsonObject().getAsJsonObject("create").get("error").getAsJsonObject().get("reason").getAsString()
        );
    }

    @Test
    public void bulkOperationWithMultipleDelete() throws IOException {
        String index = "twitter";
        String type = "tweet";
        Bulk bulk = new Bulk.Builder()
                .addAction(new Delete.Builder("1").index(index).type(type).build())
                .addAction(new Delete.Builder("2").index(index).type(type).build())
                .build();

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        assertEquals(2, items.size());
        assertEquals(0, result.getFailedItems().size());

        BulkResult.BulkResultItem firstItem = items.get(0);
        assertEquals("delete", firstItem.operation);
        assertEquals(index, firstItem.index);
        assertEquals(type, firstItem.type);
        assertEquals("1", firstItem.id);
        assertEquals(404, firstItem.status);
        assertNull(firstItem.error);

        BulkResult.BulkResultItem secondItem = items.get(1);
        assertEquals("delete", secondItem.operation);
        assertEquals(index, secondItem.index);
        assertEquals(type, secondItem.type);
        assertEquals("2", secondItem.id);
        assertEquals(404, secondItem.status);
        assertNull(secondItem.error);
    }

    @Test
    public void bulkOperationWithMultipleIndexAndDelete() throws IOException {
        String index = "twitter";
        String type = "tweet";
        Map<String, String> source = new HashMap<String, String>();
        source.put("field", "value");
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index(index).type(type).id("1").build())
                .addAction(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build())
                .addAction(new Delete.Builder("1").index(index).type(type).build())
                .addAction(new Delete.Builder("2").index(index).type(type).build())
                .build();

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        List<BulkResult.BulkResultItem> failedItems = result.getFailedItems();
        assertEquals(4, items.size());
        assertEquals(0, failedItems.size());
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

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        List<BulkResult.BulkResultItem> failedItems = result.getFailedItems();
        assertEquals(2, items.size());
        assertEquals(0, failedItems.size());
    }

}
