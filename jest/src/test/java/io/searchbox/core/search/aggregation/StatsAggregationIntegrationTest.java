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
public class StatsAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "stats_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetStatsAggregation()
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
                "        \"stats1\" : {\n" +
                "            \"stats\" : {\n" +
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

        StatsAggregation stats = result.getAggregations().getStatsAggregation("stats1");
        assertEquals("stats1", stats.getName());
        assertEquals(new Double(2.5) , stats.getAvg());
        assertTrue(2L == stats.getCount());
        assertEquals(new Double(3) , stats.getMax());
        assertEquals(new Double(2) , stats.getMin());
        assertEquals(new Double(5) , stats.getSum());

        Aggregation aggregation = result.getAggregations().getAggregation("stats1", StatsAggregation.class);
        assertTrue(aggregation instanceof StatsAggregation);
        StatsAggregation statsByType = (StatsAggregation) aggregation;
        assertEquals(stats, statsByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("stats1", StatsAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof StatsAggregation);
        StatsAggregation statsWithMap = (StatsAggregation) aggregations.get(0);
        assertEquals(stats, statsWithMap);
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
                "        \"stats1\" : {\n" +
                "            \"stats\" : {\n" +
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
        StatsAggregation stats = result.getAggregations().getStatsAggregation("stats1");
        assertEquals("stats1", stats.getName());
        assertNull(stats.getAvg());
        assertTrue(0L == stats.getCount());
        assertNull(stats.getMax());
        assertNull(stats.getMin());
        assertNull(stats.getSum());


        Aggregation aggregation = result.getAggregations().getAggregation("stats1", StatsAggregation.class);
        assertTrue(aggregation instanceof StatsAggregation);
        StatsAggregation statsByType = (StatsAggregation) aggregation;
        assertEquals(stats, statsByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("stats1", StatsAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof StatsAggregation);
        StatsAggregation statsWithMap = (StatsAggregation) aggregations.get(0);
        assertEquals(stats, statsWithMap);
    }
}
