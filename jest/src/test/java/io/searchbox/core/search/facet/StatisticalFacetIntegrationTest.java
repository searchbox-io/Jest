package io.searchbox.core.search.facet;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author ferhat
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class StatisticalFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testQuery() throws IOException {
        createIndex("statistical_facet");
        client().admin().indices().putMapping(new PutMappingRequest("statistical_facet")
                .type("document")
                .source("{\"document\":{\"properties\":{\"price\":{\"store\":true,\"type\":\"integer\"}}}}")
        ).actionGet();

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"facets\" : {\n" +
                "        \"stat1\" : {\n" +
                "            \"statistical\" : {\n" +
                "                \"field\" : \"price\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "} ";

        for (int i = 0; i < 2; i++) {
            Index index = new Index.Builder("{\"price\":\"10\"}").index("statistical_facet").type("document").refresh(true).build();
            client.execute(index);
        }

        Index index = new Index.Builder("{\"price\":\"32\"}").index("statistical_facet").type("document").refresh(true).build();
        client.execute(index);

        Search search = new Search.Builder(query)
                .addType("document")
                .addIndex("statistical_facet")
                .build();
        JestResult result = client.execute(search);
        List<StatisticalFacet> statisticalFacets = result.getFacets(StatisticalFacet.class);

        assertEquals(1, statisticalFacets.size());
        StatisticalFacet statisticalFacet = statisticalFacets.get(0);
        assertEquals("stat1", statisticalFacet.getName());
        assertEquals(3, statisticalFacet.getCount().longValue());
        assertEquals(52, statisticalFacet.getTotal().intValue());
        assertEquals(10, statisticalFacet.getMin().intValue());
        assertEquals(32, statisticalFacet.getMax().intValue());
        assertEquals(17, statisticalFacet.getMean().intValue());
        assertEquals(1224, statisticalFacet.getSumOfSquares().intValue());
        assertEquals(107, statisticalFacet.getVariance().intValue());
        assertEquals(10, statisticalFacet.getStdDeviation().intValue());
    }
}
