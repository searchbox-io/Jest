package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.params.Parameters;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Dogukan Sonmez
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class BulkIntegrationTest extends AbstractIntegrationTest {

    public static final String INDEX = "twitter";
    public static final String TYPE = "tweet";

    @Before
    public void createIndex() throws IOException {
        CreateIndex createIndex = new CreateIndex.Builder(INDEX).build();

        JestResult result = client.execute(createIndex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void bulkOperationWithCustomGson() throws Exception {
        String index = INDEX;
        String type = TYPE;
        String id = "1";
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(2013, 0, 1, 0, 0, 0); // 2013-01-01 00:00:00 GMT
        Date date = calendar.getTime();
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

        GetResponse getResponse = client().get(new GetRequest(INDEX, TYPE, "1")).actionGet(5000);
        assertNotNull(getResponse);
        // use date formatter to avoid timezone issues when testing
        SimpleDateFormat df = new SimpleDateFormat(dateStyle, Locale.UK);
        assertEquals(df.format(date), getResponse.getSourceAsMap().get("user"));
    }

    @Test
    public void bulkOperationWithIndex() throws IOException {
        String index = INDEX;
        String type = TYPE;
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
        String index = INDEX;
        String type = TYPE;
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

        GetResponse getResponse = client().get(new GetRequest(INDEX, TYPE, "1")).actionGet();
        assertNotNull(getResponse);
        assertEquals(new Gson().toJson(source1), getResponse.getSourceAsString());
    }

    @Test
    public void bulkOperationWithIndexWithSourceIncludingWhitespace() throws IOException, JSONException {
        String index = INDEX;
        String type = TYPE;
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

        GetResponse getResponse = client().get(new GetRequest(INDEX, TYPE, "1")).actionGet();
        assertNotNull(getResponse);
        assertEquals(new Gson().toJson(source1), getResponse.getSourceAsString());

        getResponse = client().get(new GetRequest(INDEX, TYPE, "2")).actionGet();
        assertNotNull(getResponse);
        JSONAssert.assertEquals(source2, getResponse.getSourceAsString(), false);
    }

    @Test
    public void bulkOperationWithIndexWithSourceIncludingLineBreak() throws IOException {
        String index = INDEX;
        String type = TYPE;
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
        String index = INDEX;
        String type = TYPE;
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
        String index = INDEX;
        String type = TYPE;
        String id = "1";
        String script =  "{ \"script\" : { \"inline\": \"ctx._source.user += params.tag\", \"params\" : {\"tag\" : \"_rob\"}}}";
        Map<String, String> source = new HashMap<>();
        source.put("user", "kimchy");
        Bulk bulk = new Bulk.Builder()
                .addAction(new Index.Builder(source).index(index).type(type).id(id).build())
                .addAction(new Update.Builder(script).index(index).type(type).id(id).build())
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

        GetResponse getResponse = client().get(new GetRequest(INDEX, TYPE, "1")).actionGet();
        assertNotNull(getResponse);
        assertEquals("{\"user\":\"kimchy_rob\"}", getResponse.getSourceAsString());
    }

    @Test
    public void bulkOperationWithIndexJsonSource() throws IOException {
        String index = INDEX;
        String type = TYPE;
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
        String index = INDEX;
        String type = TYPE;
        String id = "1";
        Bulk bulk = new Bulk.Builder()
                .addAction(new Delete.Builder(id).index(index).type(type).id(id).build())
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
        String index1 = INDEX;
        String type1 = TYPE;
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
        String index = INDEX;
        String type = TYPE;
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
        String index = INDEX;
        String type = TYPE;
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
        String index = INDEX;
        String type = TYPE;
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
                .defaultIndex(INDEX)
                .defaultType(TYPE)
                .addAction(modelList)
                .build();

        BulkResult result = client.execute(bulk);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<BulkResult.BulkResultItem> items = result.getItems();
        List<BulkResult.BulkResultItem> failedItems = result.getFailedItems();
        assertEquals(2, items.size());
        assertEquals(0, failedItems.size());
    }

    @After
    public void deleteIndex() throws IOException {
        DeleteIndex indicesExists = new DeleteIndex.Builder(INDEX).build();
        JestResult result = client.execute(indicesExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }
}
