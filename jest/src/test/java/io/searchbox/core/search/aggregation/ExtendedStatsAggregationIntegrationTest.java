package io.searchbox.core.search.aggregation;

import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cfstout
 */
@ESIntegTestCase.ClusterScope (scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class ExtendedStatsAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "extended_stats_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetExtendedStatsAggregation()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"num\":{\"store\":true,\"type\":\"integer\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"num\":2}");
        index(INDEX, TYPE, null, "{\"num\":3}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"extended_stats1\" : {\n" +
                "            \"extended_stats\" : {\n" +
                "                \"field\" : \"num\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        Search search = new Search.Builder(query)
                .addIndex(INDEX)
                .addType(TYPE)
                .build();
        SearchResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        ExtendedStatsAggregation extendedStats = result.getAggregations().getExtendedStatsAggregation("extended_stats1");
        assertEquals("extended_stats1", extendedStats.getName());
        assertEquals(new Double(2.5) , extendedStats.getAvg());
        assertTrue(2L == extendedStats.getCount());
        assertEquals(new Double(3) , extendedStats.getMax());
        assertEquals(new Double(2) , extendedStats.getMin());
        assertEquals(new Double(5) , extendedStats.getSum());
        assertEquals(new Double(.5), extendedStats.getStdDeviation());
        assertEquals(new Double(13), extendedStats.getSumOfSquares());
        assertEquals(new Double(.25), extendedStats.getVariance());

        Aggregation aggregation = result.getAggregations().getAggregation("extended_stats1", ExtendedStatsAggregation.class);
        assertTrue(aggregation instanceof ExtendedStatsAggregation);
        ExtendedStatsAggregation extendedStatsByType = (ExtendedStatsAggregation) aggregation;
        assertEquals(extendedStats, extendedStatsByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("extended_stats1", ExtendedStatsAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof ExtendedStatsAggregation);
        ExtendedStatsAggregation extendedStatsWithMap = (ExtendedStatsAggregation) aggregations.get(0);
        assertEquals(extendedStats, extendedStatsWithMap);
    }

    @Test
    public void testBadAggregationQueryResult()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"num\":{\"store\":true,\"type\":\"integer\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"num\":2}");
        index(INDEX, TYPE, null, "{\"num\":3}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"extended_stats1\" : {\n" +
                "            \"extended_stats\" : {\n" +
                "                \"field\" : \"bad_field\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        Search search = new Search.Builder(query)
                .addIndex(INDEX)
                .addType(TYPE)
                .build();
        SearchResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        ExtendedStatsAggregation extendedStats = result.getAggregations().getExtendedStatsAggregation("extended_stats1");
        assertEquals("extended_stats1", extendedStats.getName());
        assertNull(extendedStats.getAvg());
        assertTrue(0L == extendedStats.getCount());
        assertNull(extendedStats.getMax());
        assertNull(extendedStats.getMin());
        assertNull(extendedStats.getSum());
        assertNull(extendedStats.getStdDeviation());
        assertNull(extendedStats.getSumOfSquares());
        assertNull(extendedStats.getVariance());


        Aggregation aggregation = result.getAggregations().getAggregation("extended_stats1", ExtendedStatsAggregation.class);
        assertTrue(aggregation instanceof ExtendedStatsAggregation);
        ExtendedStatsAggregation extendedStatsByType = (ExtendedStatsAggregation) aggregation;
        assertEquals(extendedStats, extendedStatsByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("extended_stats1", ExtendedStatsAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof ExtendedStatsAggregation);
        ExtendedStatsAggregation extendedStatsWithMap = (ExtendedStatsAggregation) aggregations.get(0);
        assertEquals(extendedStats, extendedStatsWithMap);
    }
}
