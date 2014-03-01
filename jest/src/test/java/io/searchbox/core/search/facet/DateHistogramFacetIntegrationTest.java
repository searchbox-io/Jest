package io.searchbox.core.search.facet;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.params.Parameters;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author ferhat
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class DateHistogramFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testQuery() throws IOException {
        createIndex("date_histogram_facet");
        client().admin().indices().putMapping(new PutMappingRequest("date_histogram_facet")
                .type("document")
                .source("{\"document\":{\"properties\":{\"delivery\":{\"store\":true,\"type\":\"date\"}}}}")
        ).actionGet();

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"facets\" : {\n" +
                "        \"histo1\" : {\n" +
                "            \"date_histogram\" : {\n" +
                "                \"field\" : \"delivery\",\n" +
                "                \"interval\" : \"day\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        for (int i = 0; i < 2; i++) {
            Index index = new Index.Builder("{\"delivery\":\"2013-02-04\"}")
                    .index("date_histogram_facet")
                    .type("document")
                    .setParameter(Parameters.REFRESH, true)
                    .build();
            client.execute(index);
        }

        Index index = new Index.Builder("{\"delivery\":\"2013-02-01\"}")
                .index("date_histogram_facet")
                .type("document")
                .setParameter(Parameters.REFRESH, true)
                .build();
        client.execute(index);

        index = new Index.Builder("{\"delivery\":\"2013-02-03\"}")
                .index("date_histogram_facet")
                .type("document")
                .setParameter(Parameters.REFRESH, true)
                .build();
        client.execute(index);

        Search search = (Search) new Search.Builder(query)
                .addIndex("date_histogram_facet")
                .addType("document")
                .build();
        JestResult result = client.execute(search);
        List<DateHistogramFacet> dateHistogramFacets = result.getFacets(DateHistogramFacet.class);

        assertEquals(1, dateHistogramFacets.size());
        DateHistogramFacet histogramFacetFirst = dateHistogramFacets.get(0);
        assertEquals("histo1", histogramFacetFirst.getName());

        List<DateHistogramFacet.DateHistogram> histograms = histogramFacetFirst.getDateHistograms();
        assertEquals(3, histograms.size());
        assertTrue(1L == histograms.get(0).getCount());
        assertTrue(1359676800000L == histograms.get(0).getTime());
        assertTrue(1L == histograms.get(1).getCount());
        assertTrue(1359849600000L == histograms.get(1).getTime());
        assertTrue(2L == histograms.get(2).getCount());
        assertTrue(1359936000000L == histograms.get(2).getTime());
    }
}
