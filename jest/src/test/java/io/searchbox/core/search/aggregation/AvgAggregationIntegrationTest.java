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
public class AvgAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "avg_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetAvgAggregation()
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
                "        \"avg1\" : {\n" +
                "            \"avg\" : {\n" +
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

        AvgAggregation average = result.getAggregations().getAvgAggregation("avg1");
        assertEquals("avg1", average.getName());
        assertEquals(new Double(2.5) , average.getAvg());

        Aggregation aggregation = result.getAggregations().getAggregation("avg1", AvgAggregation.class);
        assertTrue(aggregation instanceof AvgAggregation);
        AvgAggregation averageByType = (AvgAggregation) aggregation;
        assertEquals(average, averageByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("avg1", AvgAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof AvgAggregation);
        AvgAggregation averageWithMap = (AvgAggregation) aggregations.get(0);
        assertEquals(average, averageWithMap);
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
                "        \"avg1\" : {\n" +
                "            \"avg\" : {\n" +
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
        AvgAggregation average = result.getAggregations().getAvgAggregation("avg1");
        assertNull(average.getAvg());

        Aggregation aggregation = result.getAggregations().getAggregation("avg1", AvgAggregation.class);
        assertTrue(aggregation instanceof AvgAggregation);
        AvgAggregation averageByType = (AvgAggregation) aggregation;
        assertEquals(average, averageByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("avg1", AvgAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof AvgAggregation);
        AvgAggregation averageWithMap = (AvgAggregation) aggregations.get(0);
        assertEquals(average, averageWithMap);
    }
}
