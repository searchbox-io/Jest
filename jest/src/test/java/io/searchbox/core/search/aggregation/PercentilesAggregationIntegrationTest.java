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
public class PercentilesAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "percentiles_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetPercentilesAggregation()
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
                "            \"percentiles\" : {\n" +
                "                \"field\" : \"response_millis\"\n" +
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

        PercentilesAggregation percentiles = result.getAggregations().getPercentilesAggregation("percent1");
        assertEquals("percent1", percentiles.getName());
        assertEquals(7, percentiles.getPercentiles().size());

        assertEquals(new Double(75.0),percentiles.getPercentiles().get("1.0"));
        assertEquals(new Double(75.0),percentiles.getPercentiles().get("5.0"));
        assertEquals(new Double(75.0),percentiles.getPercentiles().get("25.0"));
        assertEquals(new Double(75.0),percentiles.getPercentiles().get("50.0"));
        assertEquals(new Double(85.0), percentiles.getPercentiles().get("75.0"));
        assertEquals(new Double(115.0), percentiles.getPercentiles().get("95.0"));
        assertEquals(new Double(115.0), percentiles.getPercentiles().get("99.0"));

        Aggregation aggregation = result.getAggregations().getAggregation("percent1", PercentilesAggregation.class);
        assertTrue(aggregation instanceof PercentilesAggregation);
        PercentilesAggregation percentilesByType = (PercentilesAggregation) aggregation;
        assertEquals(percentiles, percentilesByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("percent1", PercentilesAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof PercentilesAggregation);
        PercentilesAggregation percentilesWithMap = (PercentilesAggregation) aggregations.get(0);
        assertEquals(percentiles, percentilesWithMap);
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
                "            \"percentiles\" : {\n" +
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
        PercentilesAggregation percentiles = result.getAggregations().getPercentilesAggregation("percent1");
        assertTrue(percentiles.getPercentiles().isEmpty());

        Aggregation aggregation = result.getAggregations().getAggregation("percent1", PercentilesAggregation.class);
        assertTrue(aggregation instanceof PercentilesAggregation);
        PercentilesAggregation percentilesByType = (PercentilesAggregation) aggregation;
        assertEquals(percentiles, percentilesByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("percent1", PercentilesAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof PercentilesAggregation);
        PercentilesAggregation percentilesWithMap = (PercentilesAggregation) aggregations.get(0);
        assertEquals(percentiles, percentilesWithMap);
    }
}
