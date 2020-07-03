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
public class ValueCountAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "value_count_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetValueCountAggregation()
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
                "        \"value_count1\" : {\n" +
                "            \"value_count\" : {\n" +
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

        ValueCountAggregation valueCount = result.getAggregations().getValueCountAggregation("value_count1");
        assertEquals("value_count1", valueCount.getName());
        assertTrue(0L == valueCount.getValueCount());

        Aggregation aggregation = result.getAggregations().getAggregation("value_count1", ValueCountAggregation.class);
        assertTrue(aggregation instanceof ValueCountAggregation);
        ValueCountAggregation valueCountByType = (ValueCountAggregation) aggregation;
        assertEquals(valueCount, valueCountByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("value_count1", ValueCountAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof ValueCountAggregation);
        ValueCountAggregation valueCountWithMap = (ValueCountAggregation) aggregations.get(0);
        assertEquals(valueCount, valueCountWithMap);
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
                "        \"value_count1\" : {\n" +
                "            \"value_count\" : {\n" +
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
        ValueCountAggregation valueCount = result.getAggregations().getValueCountAggregation("value_count1");
        assertTrue(0L == valueCount.getValueCount());

        Aggregation aggregation = result.getAggregations().getAggregation("value_count1", ValueCountAggregation.class);
        assertTrue(aggregation instanceof ValueCountAggregation);
        ValueCountAggregation valueCountByType = (ValueCountAggregation) aggregation;
        assertEquals(valueCount, valueCountByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("value_count1", ValueCountAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof ValueCountAggregation);
        ValueCountAggregation valueCountWithMap = (ValueCountAggregation) aggregations.get(0);
        assertEquals(valueCount, valueCountWithMap);
    }
}
