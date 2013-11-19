package io.searchbox.core.search.facet;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author ferhat
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class QueryFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "query_facet")
    public void testQuery() {

        String query = "{\n" +
                "            \"facets\" : {\n" +
                "            \"wow_facet\" : {\n" +
                "                \"query\" : {\n" +
                "                    \"term\" : { \"tag\" : \"wow\" }\n" +
                "                }\n" +
                "            },\n" +
                "            \"none_user_facet\" : {\n" +
                "                \"query\" : {\n" +
                "                    \"term\" : { \"user\" : \"none\" }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        }";

        try {
            for (int i = 0; i < 2; i++) {
                Index index = new Index.Builder("{\"tag\":\"wow\", \"user\":\"root\"}").index("query_facet").type("document").refresh(true).build();
                client.execute(index);
            }

            Index index = new Index.Builder("{\"tag\":\"test\", \"user\":\"none\"}").index("query_facet").type("document").refresh(true).build();
            client.execute(index);

            Search search = new Search.Builder(query)
                    .addIndex("query_facet")
                    .addType("document")
                    .build();
            JestResult result = client.execute(search);
            List<QueryFacet> filterFacets = result.getFacets(QueryFacet.class);

            assertEquals(2, filterFacets.size());
            QueryFacet queryFacetFirst = filterFacets.get(0);

            assertEquals("wow_facet", queryFacetFirst.getName());
            assertEquals(2L, queryFacetFirst.getCount().longValue());

            QueryFacet queryFacetSecond = filterFacets.get(1);

            assertEquals("none_user_facet", queryFacetSecond.getName());
            assertEquals(1L, queryFacetSecond.getCount().longValue());

        } catch (Exception e) {
            fail("Failed during terms facet tests " + e.getMessage());
        }
    }
}