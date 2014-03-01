package io.searchbox.core.search.sort;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author ferhat
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class SortIntegrationTest extends AbstractIntegrationTest {

    String query = "{\"query\":{ \"match_all\" : { }}}";

    @Test
    public void searchWithValidQueryAndSort() throws IOException {
        createIndex("ranker");
        client().admin().indices().putMapping(new PutMappingRequest("ranker")
                .type("ranking")
                .source("{\"ranking\":{\"properties\":{\"rank\":{\"store\":true,\"type\":\"integer\"}}}}")
        ).actionGet();

        client().index(new IndexRequest("ranker", "ranking").source("{\"rank\":10}").refresh(true)).actionGet();
        client().index(new IndexRequest("ranker", "ranking").source("{\"rank\":5}").refresh(true)).actionGet();
        client().index(new IndexRequest("ranker", "ranking").source("{\"rank\":8}").refresh(true)).actionGet();

        Sort sort = new Sort("rank");
        Search search = new Search.Builder(query)
                .addSort(sort)
                .addIndex("ranker")
                .addType("ranking")
                .build();
        JestResult result = client.execute(search);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        List hits = ((List) ((Map) result.getJsonMap().get("hits")).get("hits"));
        assertEquals(3, hits.size());
        assertEquals(5D, ((Map) ((Map) hits.get(0)).get("_source")).get("rank"));
        assertEquals(8D, ((Map) ((Map) hits.get(1)).get("_source")).get("rank"));
        assertEquals(10D, ((Map) ((Map) hits.get(2)).get("_source")).get("rank"));
    }

    @Test
    public void searchWithValidQuery() throws IOException {
        createIndex("cvbank");

        Index index = new Index.Builder("{\"user\":\"kimchy\"}").refresh(true).build();
        client.execute(index);
        JestResult result = client.execute(new Search.Builder(query).build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }
}
