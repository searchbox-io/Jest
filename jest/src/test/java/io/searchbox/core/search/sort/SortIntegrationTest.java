package io.searchbox.core.search.sort;

import com.google.gson.*;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Search;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * @author ferhat
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class SortIntegrationTest extends AbstractIntegrationTest {

    String query = "{\"query\":{ \"match_all\" : { }}}";
    String index = "ranker";
    String anotherIndex = "another_ranker";
    String type = "ranking";

    @Before
    public void setup() {
        createIndex(index);
        client().admin().indices().putMapping(new PutMappingRequest(index)
                        .type(type)
                        .source("{\"ranking\":{\"properties\":{\"rank\":{\"store\":true,\"type\":\"integer\"}}}}", XContentType.JSON)
        ).actionGet();

        client().index(new IndexRequest(index, type).source("{\"rank\":10}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet();
        client().index(new IndexRequest(index, type).source("{\"rank\":5}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet();
        client().index(new IndexRequest(index, type).source("{\"rank\":8}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet();

        createIndex(anotherIndex);
        client().admin().indices().putMapping(new PutMappingRequest(anotherIndex)
                .type(type)
                .source("{\"ranking\":{\"properties\":{\"rank\":{\"store\":true,\"type\":\"integer\"}, \"rankType\":{\"store\":true,\"type\":\"integer\"}}}}",
                        XContentType.JSON)
        ).actionGet();

        client().index(new IndexRequest(anotherIndex, type).source("{\"rank\":90, \"rankType\":0}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet();
        client().index(new IndexRequest(anotherIndex, type).source("{\"rank\":90, \"rankType\":1}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet();
        client().index(new IndexRequest(anotherIndex, type).source("{\"rank\":101, \"rankType\":1}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet();

        ensureSearchable(index, anotherIndex);
    }

    @Test
    public void searchWithSimpleFieldSort() throws IOException {
        Sort sort = new Sort("rank");
        Search search = new Search.Builder(query)
                .addSort(sort)
                .addIndex(index)
                .addType(type)
                .build();
        JestResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonObject object = (JsonObject) result.getJsonObject().get("hits");
        JsonArray hits = (JsonArray) object.get("hits");
        assertEquals(3, hits.size());
        assertEquals(5, ((JsonObject) ((JsonObject) hits.get(0)).get("_source")).get("rank").getAsInt());
        assertEquals(8, ((JsonObject) ((JsonObject) hits.get(1)).get("_source")).get("rank").getAsInt());
        assertEquals(10, ((JsonObject) ((JsonObject) hits.get(2)).get("_source")).get("rank").getAsInt());
    }

    @Test
    public void searchWithCustomSort() throws IOException {
        Sort sort = new Sort("rank", Sort.Sorting.DESC);
        Search search = new Search.Builder(query)
                .addSort(sort)
                .addIndex(index)
                .addType(type)
                .build();
        JestResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonObject object = (JsonObject) result.getJsonObject().get("hits");
        JsonArray hits = (JsonArray) object.get("hits");
        assertEquals(3, hits.size());
        assertEquals(5, ((JsonObject) ((JsonObject) hits.get(2)).get("_source")).get("rank").getAsInt());
        assertEquals(8, ((JsonObject) ((JsonObject) hits.get(1)).get("_source")).get("rank").getAsInt());
        assertEquals(10, ((JsonObject) ((JsonObject) hits.get(0)).get("_source")).get("rank").getAsInt());
    }

    @Test
    public void searchWithMultiIndexSortFieldUnmapped() throws IOException {
        List<Sort> sorts = new ArrayList<>();
        sorts.add(new Sort("rank"));
        Sort sort = new Sort("rankType", Sort.Sorting.DESC);
        sorts.add(sort);

        Search search = new Search.Builder(query)
                .addSort(sorts)
                .addIndex(index)
                .addIndex(anotherIndex)
                .addType(type)
                .build();
        JestResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonObject object = (JsonObject) result.getJsonObject().get("hits");
        JsonArray hits = (JsonArray) object.get("hits");
        assertEquals(3, hits.size());
        assertNotEquals(index, ((JsonObject) hits.get(0)).get("_index").getAsString());
        assertNotEquals(index, ((JsonObject) hits.get(1)).get("_index").getAsString());
        assertNotEquals(index, ((JsonObject) hits.get(2)).get("_index").getAsString());

        sorts.remove(1);
        sort.setUnmappedType("integer");
        sorts.add(sort);

        search = new Search.Builder(query)
                .addSort(sorts)
                .addIndex(index)
                .addIndex(anotherIndex)
                .addType(type)
                .build();
        result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        object = (JsonObject) result.getJsonObject().get("hits");
        hits = (JsonArray) object.get("hits");
        assertEquals(6, hits.size());
        assertEquals(5, ((JsonObject) ((JsonObject) hits.get(0)).get("_source")).get("rank").getAsInt());
        assertEquals(index, ((JsonObject) hits.get(0)).get("_index").getAsString());
        assertEquals(8, ((JsonObject) ((JsonObject) hits.get(1)).get("_source")).get("rank").getAsInt());
        assertEquals(index, ((JsonObject) hits.get(1)).get("_index").getAsString());
        assertEquals(10, ((JsonObject) ((JsonObject) hits.get(2)).get("_source")).get("rank").getAsInt());
        assertEquals(index, ((JsonObject) hits.get(2)).get("_index").getAsString());
        assertEquals(90, ((JsonObject) ((JsonObject) hits.get(3)).get("_source")).get("rank").getAsInt());
        assertEquals(1, ((JsonObject) ((JsonObject) hits.get(3)).get("_source")).get("rankType").getAsInt());
        assertEquals(anotherIndex, ((JsonObject) hits.get(3)).get("_index").getAsString());
        assertEquals(90, ((JsonObject) ((JsonObject) hits.get(4)).get("_source")).get("rank").getAsInt());
        assertEquals(0, ((JsonObject) ((JsonObject) hits.get(4)).get("_source")).get("rankType").getAsInt());
        assertEquals(anotherIndex, ((JsonObject) hits.get(4)).get("_index").getAsString());
        assertEquals(101, ((JsonObject) ((JsonObject) hits.get(5)).get("_source")).get("rank").getAsInt());
        assertEquals(1, ((JsonObject) ((JsonObject) hits.get(5)).get("_source")).get("rankType").getAsInt());
        assertEquals(anotherIndex, ((JsonObject) hits.get(5)).get("_index").getAsString());
    }

}
