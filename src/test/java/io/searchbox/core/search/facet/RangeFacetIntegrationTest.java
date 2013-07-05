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

import static junit.framework.Assert.*;

/**
 * @author ferhat
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class RangeFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "range_facet",
            mappings = {
                    @ElasticsearchMapping(typeName = "document",
                            properties = {
                                    @ElasticsearchMappingField(name = "width", store = ElasticsearchMappingField.Store.Yes,
                                            type = ElasticsearchMappingField.Types.Integer)
                            })

            })
    public void testQuery() {
        String query = "{\n" +
                "   \"query\":{\n" +
                "      \"match_all\":{\n" +
                "\n" +
                "      }\n" +
                "   },\n" +
                "   \"facets\":{\n" +
                "      \"range1\":{\n" +
                "         \"range\":{\n" +
                "            \"field\":\"width\",\n" +
                "            \"ranges\":[\n" +
                "               {\n" +
                "                  \"to\":50\n" +
                "               },\n" +
                "               {\n" +
                "                  \"from\":70,\n" +
                "                  \"to\":120\n" +
                "               },\n" +
                "               {\n" +
                "                  \"from\":150\n" +
                "               }\n" +
                "            ]\n" +
                "         }\n" +
                "      },\n" +
                "      \"range2\":{\n" +
                "         \"range\":{\n" +
                "            \"field\":\"width\",\n" +
                "            \"ranges\":[\n" +
                "               {\n" +
                "                  \"to\":20\n" +
                "               },\n" +
                "               {\n" +
                "                  \"from\":20,\n" +
                "                  \"to\":70\n" +
                "               },\n" +
                "               {\n" +
                "                  \"from\":75\n" +
                "               }\n" +
                "            ]\n" +
                "         }\n" +
                "      }\n" +
                "   }\n" +
                "}";


        try {
            for (int i = 0; i < 2; i++) {
                Index index = new Index.Builder("{\"width\":\"80\"}").index("range_facet").type("document").refresh(true).build();
                client.execute(index);
            }

            Index index = new Index.Builder("{\"width\":\"70\"}").index("range_facet").type("document").refresh(true).build();
            client.execute(index);

            index = new Index.Builder("{\"width\":\"30\"}").index("range_facet").type("document").refresh(true).build();
            client.execute(index);

            Search search = (Search) new Search.Builder(query)
                    .addIndex("range_facet")
                    .addType("document")
                    .build();
            JestResult result = client.execute(search);
            List<RangeFacet> rangeFacetList = result.getFacets(RangeFacet.class);

            assertTrue(2 == rangeFacetList.size());

            RangeFacet rangeFacetFirst = rangeFacetList.get(0);
            assertEquals("range1", rangeFacetFirst.getName());

            assertEquals(null, rangeFacetFirst.getRanges().get(0).getFrom());
            assertEquals(50.0, rangeFacetFirst.getRanges().get(0).getTo());
            assertEquals(1L, rangeFacetFirst.getRanges().get(0).getCount().longValue());
            assertEquals(30.0, rangeFacetFirst.getRanges().get(0).getMin());
            assertEquals(30.0, rangeFacetFirst.getRanges().get(0).getMax());
            assertEquals(1L, rangeFacetFirst.getRanges().get(0).getTotalCount().longValue());
            assertEquals(30.0, rangeFacetFirst.getRanges().get(0).getTotal());
            assertEquals(30.0, rangeFacetFirst.getRanges().get(0).getMean());

            assertEquals(70.0, rangeFacetFirst.getRanges().get(1).getFrom());
            assertEquals(120.0, rangeFacetFirst.getRanges().get(1).getTo());
            assertEquals(3L, rangeFacetFirst.getRanges().get(1).getCount().longValue());
            assertEquals(70.0, rangeFacetFirst.getRanges().get(1).getMin());
            assertEquals(80.0, rangeFacetFirst.getRanges().get(1).getMax());
            assertEquals(3L, rangeFacetFirst.getRanges().get(1).getTotalCount().longValue());
            assertEquals(230.0, rangeFacetFirst.getRanges().get(1).getTotal());
            assertEquals(76.66666666666667, rangeFacetFirst.getRanges().get(1).getMean());

            assertEquals(150.0, rangeFacetFirst.getRanges().get(2).getFrom());
            assertEquals(null, rangeFacetFirst.getRanges().get(2).getTo());
            assertEquals(0L, rangeFacetFirst.getRanges().get(2).getCount().longValue());
            assertEquals(null, rangeFacetFirst.getRanges().get(2).getMin());
            assertEquals(null, rangeFacetFirst.getRanges().get(2).getMax());
            assertEquals(0L, rangeFacetFirst.getRanges().get(2).getTotalCount().longValue());
            assertEquals(0.0, rangeFacetFirst.getRanges().get(2).getTotal());
            assertEquals(0.0, rangeFacetFirst.getRanges().get(2).getMean());

            RangeFacet rangeFacetSecond = rangeFacetList.get(1);
            assertEquals("range2", rangeFacetSecond.getName());

            assertEquals(null, rangeFacetSecond.getRanges().get(0).getFrom());
            assertEquals(20.0, rangeFacetSecond.getRanges().get(0).getTo());
            assertEquals(0L, rangeFacetSecond.getRanges().get(0).getCount().longValue());
            assertEquals(null, rangeFacetSecond.getRanges().get(0).getMin());
            assertEquals(null, rangeFacetSecond.getRanges().get(0).getMax());
            assertEquals(0L, rangeFacetSecond.getRanges().get(0).getTotalCount().longValue());
            assertEquals(0.0, rangeFacetSecond.getRanges().get(0).getTotal());
            assertEquals(0.0, rangeFacetSecond.getRanges().get(0).getMean());

            assertEquals(20.0, rangeFacetSecond.getRanges().get(1).getFrom());
            assertEquals(70.0, rangeFacetSecond.getRanges().get(1).getTo());
            assertEquals(1L, rangeFacetSecond.getRanges().get(1).getCount().longValue());
            assertEquals(30.0, rangeFacetSecond.getRanges().get(1).getMin());
            assertEquals(30.0, rangeFacetSecond.getRanges().get(1).getMax());
            assertEquals(1L, rangeFacetSecond.getRanges().get(1).getTotalCount().longValue());
            assertEquals(30.0, rangeFacetSecond.getRanges().get(1).getTotal());
            assertEquals(30.0, rangeFacetSecond.getRanges().get(1).getMean());

            assertEquals(75.0, rangeFacetSecond.getRanges().get(2).getFrom());
            assertEquals(null, rangeFacetSecond.getRanges().get(2).getTo());
            assertEquals(2L, rangeFacetSecond.getRanges().get(2).getCount().longValue());
            assertEquals(80.0, rangeFacetSecond.getRanges().get(2).getMin());
            assertEquals(80.0, rangeFacetSecond.getRanges().get(2).getMax());
            assertEquals(2L, rangeFacetSecond.getRanges().get(2).getTotalCount().longValue());
            assertEquals(160.0, rangeFacetSecond.getRanges().get(2).getTotal());
            assertEquals(80.0, rangeFacetSecond.getRanges().get(2).getMean());


        } catch (Exception e) {
            fail("Failed during facet tests " + e.getMessage());
        }


    }
}
