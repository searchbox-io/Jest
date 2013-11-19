package io.searchbox.core.search.sort;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchMapping;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchMappingField;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author ferhat
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class SortIntegrationTest extends AbstractIntegrationTest {

    String query = "{\"query\":{ \"match_all\" : { }}}";

    @Test
    @ElasticsearchIndex(indexName = "ranker",
            mappings = {
                    @ElasticsearchMapping(typeName = "ranking",
                            properties = {
                                    @ElasticsearchMappingField(name = "rank", store = ElasticsearchMappingField.Store.Yes,
                                            type = ElasticsearchMappingField.Types.Integer)
                            })

            })
    public void searchWithValidQueryAndSort() {
        try {
            Index index = new Index.Builder("{\"rank\":10}").index("ranker").type("ranking").refresh(true).build();
            client.execute(index);

            index = new Index.Builder("{\"rank\":5}").index("ranker").type("ranking").refresh(true).build();
            client.execute(index);

            index = new Index.Builder("{\"rank\":8}").index("ranker").type("ranking").refresh(true).build();
            client.execute(index);

            Sort sort = new Sort("rank");
            Search search = (Search) new Search.Builder(query)
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
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "cvbank")
    public void searchWithValidQuery() {
        try {
            Index index = new Index.Builder("{\"user\":\"kimchy\"}").refresh(true).build();
            client.execute(index);
            JestResult result = client.execute(new Search.Builder(query).build());
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }
}
