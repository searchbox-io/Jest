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
public class PercentileRanksAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "percentile_ranks_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetPercentileRanksAggregation()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"response_millis\":{\"store\":true,\"type\":\"double\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"response_millis\": 75}");
        index(INDEX, TYPE, null, "{\"response_millis\": 75}");
        index(INDEX, TYPE, null, "{\"response_millis\": 75}");
        index(INDEX, TYPE, null, "{\"response_millis\": 75}");
        index(INDEX, TYPE, null, "{\"response_millis\": 115}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"percent1\" : {\n" +
                "            \"percentile_ranks\" : {\n" +
                "                \"field\" : \"response_millis\",\n" +
                "                \"values\" : [80.0, 100.0]\n" +
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

        PercentileRanksAggregation percentileRanks = result.getAggregations().getPercentileRanksAggregation("percent1");
        assertEquals("percent1", percentileRanks.getName());
        assertEquals(2, percentileRanks.getPercentileRanks().size());

        assertEquals(new Double(65.0), percentileRanks.getPercentileRanks().get("80.0"));
        assertEquals(new Double(100.0), percentileRanks.getPercentileRanks().get("100.0"));

        Aggregation aggregation = result.getAggregations().getAggregation("percent1", PercentileRanksAggregation.class);
        assertTrue(aggregation instanceof PercentileRanksAggregation);
        PercentileRanksAggregation percentileRanksByType = (PercentileRanksAggregation) aggregation;
        assertEquals(percentileRanks, percentileRanksByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("percent1", PercentileRanksAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof PercentileRanksAggregation);
        PercentileRanksAggregation percentileRanksWithMap = (PercentileRanksAggregation) aggregations.get(0);
        assertEquals(percentileRanks, percentileRanksWithMap);
    }

    @Test
    public void testBadAggregationQueryResult()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"response_millis\":{\"store\":true,\"type\":\"double\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"response_millis\": 75}");
        index(INDEX, TYPE, null, "{\"response_millis\": 75}");
        index(INDEX, TYPE, null, "{\"response_millis\": 75}");
        index(INDEX, TYPE, null, "{\"response_millis\": 75}");
        index(INDEX, TYPE, null, "{\"response_millis\": 115}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"percent1\" : {\n" +
                "            \"percentile_ranks\" : {\n" +
                "                \"field\" : \"bad_field\",\n" +
                "                \"values\" : [80.0, 100.0]\n" +
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
        PercentileRanksAggregation percentileRanks = result.getAggregations().getPercentileRanksAggregation("percent1");
        assertTrue(percentileRanks.getPercentileRanks().isEmpty());

        Aggregation aggregation = result.getAggregations().getAggregation("percent1", PercentileRanksAggregation.class);
        assertTrue(aggregation instanceof PercentileRanksAggregation);
        PercentileRanksAggregation percentileRanksByType = (PercentileRanksAggregation) aggregation;
        assertEquals(percentileRanks, percentileRanksByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("percent1", PercentileRanksAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof PercentileRanksAggregation);
        PercentileRanksAggregation percentileRanksWithMap = (PercentileRanksAggregation) aggregations.get(0);
        assertEquals(percentileRanks, percentileRanksWithMap);
    }
}
