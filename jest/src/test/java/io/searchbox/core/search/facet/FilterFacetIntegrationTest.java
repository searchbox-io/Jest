package io.searchbox.core.search.facet;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author ferhat
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class FilterFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testQuery() throws IOException {
        createIndex("filter_facet");

        String query = "{\n" +
                "            \"facets\" : {\n" +
                "            \"wow_facet\" : {\n" +
                "                \"filter\" : {\n" +
                "                    \"term\" : { \"tag\" : \"wow\" }\n" +
                "                }\n" +
                "            },\n" +
                "            \"none_user_facet\" : {\n" +
                "                \"filter\" : {\n" +
                "                    \"term\" : { \"user\" : \"none\" }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        }";

        for (int i = 0; i < 2; i++) {
            Index index = new Index.Builder("{\"tag\":\"wow\", \"user\":\"root\"}")
                    .index("filter_facet")
                    .type("document")
                    .refresh(true)
                    .build();
            client.execute(index);
        }

        Index index = new Index.Builder("{\"tag\":\"test\", \"user\":\"none\"}")
                .index("filter_facet")
                .type("document")
                .refresh(true)
                .build();
        client.execute(index);

        Search search = new Search.Builder(query)
                .addIndex("filter_facet")
                .addType("document")
                .build();
        JestResult result = client.execute(search);
        List<FilterFacet> filterFacets = result.getFacets(FilterFacet.class);

        assertEquals(2, filterFacets.size());
        FilterFacet filterFacetFirst = filterFacets.get(0);

        assertEquals("wow_facet", filterFacetFirst.getName());
        assertEquals(2L, filterFacetFirst.getCount().longValue());

        FilterFacet filterFacetSecond = filterFacets.get(1);

        assertEquals("none_user_facet", filterFacetSecond.getName());
        assertEquals(1L, filterFacetSecond.getCount().longValue());
    }
}