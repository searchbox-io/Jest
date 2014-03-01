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
public class RangeFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testQuery() throws IOException {
        createIndex("range_facet");
        client().admin().indices().putMapping(new PutMappingRequest("range_facet")
                .type("document")
                .source("{\"document\":{\"properties\":{\"width\":{\"store\":true,\"type\":\"integer\"}}}}")
        ).actionGet();

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
        assertEquals(50, rangeFacetFirst.getRanges().get(0).getTo().intValue());
        assertEquals(1L, rangeFacetFirst.getRanges().get(0).getCount().longValue());
        assertEquals(30, rangeFacetFirst.getRanges().get(0).getMin().intValue());
        assertEquals(30, rangeFacetFirst.getRanges().get(0).getMax().intValue());
        assertEquals(1L, rangeFacetFirst.getRanges().get(0).getTotalCount().longValue());
        assertEquals(30, rangeFacetFirst.getRanges().get(0).getTotal().intValue());
        assertEquals(30, rangeFacetFirst.getRanges().get(0).getMean().intValue());

        assertEquals(70, rangeFacetFirst.getRanges().get(1).getFrom().intValue());
        assertEquals(120, rangeFacetFirst.getRanges().get(1).getTo().intValue());
        assertEquals(3L, rangeFacetFirst.getRanges().get(1).getCount().longValue());
        assertEquals(70, rangeFacetFirst.getRanges().get(1).getMin().intValue());
        assertEquals(80, rangeFacetFirst.getRanges().get(1).getMax().intValue());
        assertEquals(3L, rangeFacetFirst.getRanges().get(1).getTotalCount().longValue());
        assertEquals(230, rangeFacetFirst.getRanges().get(1).getTotal().intValue());
        assertEquals(76, rangeFacetFirst.getRanges().get(1).getMean().intValue());

        assertEquals(150, rangeFacetFirst.getRanges().get(2).getFrom().intValue());
        assertEquals(null, rangeFacetFirst.getRanges().get(2).getTo());
        assertEquals(0L, rangeFacetFirst.getRanges().get(2).getCount().longValue());
        assertEquals(null, rangeFacetFirst.getRanges().get(2).getMin());
        assertEquals(null, rangeFacetFirst.getRanges().get(2).getMax());
        assertEquals(0L, rangeFacetFirst.getRanges().get(2).getTotalCount().longValue());
        assertEquals(0, rangeFacetFirst.getRanges().get(2).getTotal().intValue());
        assertEquals(0, rangeFacetFirst.getRanges().get(2).getMean().intValue());

        RangeFacet rangeFacetSecond = rangeFacetList.get(1);
        assertEquals("range2", rangeFacetSecond.getName());

        assertEquals(null, rangeFacetSecond.getRanges().get(0).getFrom());
        assertEquals(20, rangeFacetSecond.getRanges().get(0).getTo().intValue());
        assertEquals(0L, rangeFacetSecond.getRanges().get(0).getCount().longValue());
        assertEquals(null, rangeFacetSecond.getRanges().get(0).getMin());
        assertEquals(null, rangeFacetSecond.getRanges().get(0).getMax());
        assertEquals(0L, rangeFacetSecond.getRanges().get(0).getTotalCount().longValue());
        assertEquals(0, rangeFacetSecond.getRanges().get(0).getTotal().intValue());
        assertEquals(0, rangeFacetSecond.getRanges().get(0).getMean().intValue());

        assertEquals(20, rangeFacetSecond.getRanges().get(1).getFrom().intValue());
        assertEquals(70, rangeFacetSecond.getRanges().get(1).getTo().intValue());
        assertEquals(1L, rangeFacetSecond.getRanges().get(1).getCount().longValue());
        assertEquals(30, rangeFacetSecond.getRanges().get(1).getMin().intValue());
        assertEquals(30, rangeFacetSecond.getRanges().get(1).getMax().intValue());
        assertEquals(1L, rangeFacetSecond.getRanges().get(1).getTotalCount().longValue());
        assertEquals(30, rangeFacetSecond.getRanges().get(1).getTotal().intValue());
        assertEquals(30, rangeFacetSecond.getRanges().get(1).getMean().intValue());

        assertEquals(75, rangeFacetSecond.getRanges().get(2).getFrom().intValue());
        assertEquals(null, rangeFacetSecond.getRanges().get(2).getTo());
        assertEquals(2L, rangeFacetSecond.getRanges().get(2).getCount().longValue());
        assertEquals(80, rangeFacetSecond.getRanges().get(2).getMin().intValue());
        assertEquals(80, rangeFacetSecond.getRanges().get(2).getMax().intValue());
        assertEquals(2L, rangeFacetSecond.getRanges().get(2).getTotalCount().longValue());
        assertEquals(160, rangeFacetSecond.getRanges().get(2).getTotal().intValue());
        assertEquals(80, rangeFacetSecond.getRanges().get(2).getMean().intValue());
    }
}
