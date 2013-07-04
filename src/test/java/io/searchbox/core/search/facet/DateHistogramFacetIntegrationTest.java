package io.searchbox.core.search.facet;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchMapping;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchMappingField;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.params.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author ferhat
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class DateHistogramFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "date_histogram_facet",
            mappings = {
                    @ElasticsearchMapping(typeName = "document",
                            properties = {
                                    @ElasticsearchMappingField(name = "delivery", store = ElasticsearchMappingField.Store.Yes,
                                            type = ElasticsearchMappingField.Types.Date)
                            })

            })
    public void testQuery() {

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

        try {

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

        } catch (Exception e) {
            fail("Failed during facet tests " + e.getMessage());
        }
    }
}
