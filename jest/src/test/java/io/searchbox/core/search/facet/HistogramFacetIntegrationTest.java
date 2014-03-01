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
public class HistogramFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testQuery() throws IOException {
        createIndex("histogram_facet");
        client().admin().indices().putMapping(new PutMappingRequest("histogram_facet")
                .type("document")
                .source("{\"document\":{\"properties\":{\"quantity\":{\"store\":true,\"type\":\"integer\"}}}}")
        ).actionGet();

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"facets\" : {\n" +
                "        \"histo1\" : {\n" +
                "            \"histogram\" : {\n" +
                "                \"field\" : \"quantity\",\n" +
                "                \"interval\" : 100\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        for (int i = 0; i < 2; i++) {
            Index index = new Index.Builder("{\"quantity\":\"910\"}")
                    .index("histogram_facet")
                    .type("document")
                    .refresh(true)
                    .build();
            client.execute(index);
        }

        Index index = new Index.Builder("{\"quantity\":\"800\"}")
                .index("histogram_facet")
                .type("document")
                .refresh(true)
                .build();
        client.execute(index);

        index = new Index.Builder("{\"quantity\":\"1110\"}")
                .index("histogram_facet")
                .type("document")
                .refresh(true)
                .build();
        client.execute(index);

        Search search = new Search.Builder(query)
                .addIndex("histogram_facet")
                .addType("document")
                .build();
        JestResult result = client.execute(search);
        List<HistogramFacet> histogramFacets = result.getFacets(HistogramFacet.class);

        assertEquals(1, histogramFacets.size());
        HistogramFacet histogramFacetFirst = histogramFacets.get(0);
        assertEquals("histo1", histogramFacetFirst.getName());

        List<HistogramFacet.Histogram> histograms = histogramFacetFirst.getHistograms();
        assertEquals(3, histograms.size());
        assertTrue(1L == histograms.get(0).getCount());
        assertTrue(800L == histograms.get(0).getKey());
        assertTrue(2L == histograms.get(1).getCount());
        assertTrue(900L == histograms.get(1).getKey());
        assertTrue(1L == histograms.get(2).getCount());
        assertTrue(1100L == histograms.get(2).getKey());
    }
}
