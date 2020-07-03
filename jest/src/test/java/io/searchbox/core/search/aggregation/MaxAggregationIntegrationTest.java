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
public class MaxAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "max_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetMaxAggregation()
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
                "        \"max1\" : {\n" +
                "            \"max\" : {\n" +
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

        MaxAggregation max = result.getAggregations().getMaxAggregation("max1");
        assertEquals("max1", max.getName());
        assertEquals(new Double(3) , max.getMax());

        Aggregation aggregation = result.getAggregations().getAggregation("max1", MaxAggregation.class);
        assertTrue(aggregation instanceof MaxAggregation);
        MaxAggregation maxByType = (MaxAggregation) aggregation;
        assertEquals(max, maxByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("max1", MaxAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof MaxAggregation);
        MaxAggregation maxWithMap = (MaxAggregation) aggregations.get(0);
        assertEquals(max, maxWithMap);
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
                "        \"max1\" : {\n" +
                "            \"max\" : {\n" +
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
        MaxAggregation max = result.getAggregations().getMaxAggregation("max1");
        assertNull(max.getMax());

        Aggregation aggregation = result.getAggregations().getAggregation("max1", MaxAggregation.class);
        assertTrue(aggregation instanceof MaxAggregation);
        MaxAggregation maxByType = (MaxAggregation) aggregation;
        assertEquals(max, maxByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("max1", MaxAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof MaxAggregation);
        MaxAggregation maxWithMap = (MaxAggregation) aggregations.get(0);
        assertEquals(max, maxWithMap);
    }
}
