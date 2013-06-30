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
public class StatisticalFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "statistical_facet",
            mappings = {
                    @ElasticsearchMapping(typeName = "document",
                            properties = {
                                    @ElasticsearchMappingField(name = "price", store = ElasticsearchMappingField.Store.Yes,
                                            type = ElasticsearchMappingField.Types.Integer)
                            })

            })
    public void testQuery() {

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


        try {
            for (int i = 0; i < 2; i++) {
                Index index = new Index.Builder("{\"price\":\"10\"}").index("statistical_facet").type("document").refresh(true).build();
                client.execute(index);
            }

            Index index = new Index.Builder("{\"price\":\"32\"}").index("statistical_facet").type("document").refresh(true).build();
            client.execute(index);

            Search search = (Search) new Search.Builder(query)
                    .addType("document")
                    .addIndex("statistical_facet")
                    .build();
            JestResult result = client.execute(search);
            List<StatisticalFacet> statisticalFacets = result.getFacets(StatisticalFacet.class);

            assertEquals(1, statisticalFacets.size());
            StatisticalFacet statisticalFacet = statisticalFacets.get(0);
            assertEquals("stat1", statisticalFacet.getName());
            assertEquals(3, statisticalFacet.getCount().longValue());
            assertEquals(52.0, statisticalFacet.getTotal());
            assertEquals(10.0, statisticalFacet.getMin());
            assertEquals(32.0, statisticalFacet.getMax());
            assertEquals(17.333333333333332, statisticalFacet.getMean());
            assertEquals(1224.0, statisticalFacet.getSumOfSquares());
            assertEquals(107.55555555555554, statisticalFacet.getVariance());
            assertEquals(10.370899457402697, statisticalFacet.getStdDeviation());

        } catch (Exception e) {
            fail("Failed during terms facet tests " + e.getMessage());
        }
    }
}
