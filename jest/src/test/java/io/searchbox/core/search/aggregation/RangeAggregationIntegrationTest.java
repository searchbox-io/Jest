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
public class RangeAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "range_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetRangeAggregation()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"num\":{\"store\":true,\"type\":\"integer\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"num\": 17}");
        index(INDEX, TYPE, null, "{\"num\":24}");
        index(INDEX, TYPE, null, "{\"num\":42}");
        index(INDEX, TYPE, null, "{\"num\":16}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"range1\" : {\n" +
                "            \"range\" : {\n" +
                "                \"field\" : \"num\",\n" +
                "                \"ranges\" : [\n" +
                "                   { \"to\": 20},\n" +
                "                   { \"from\": 20, \"to\": 40},\n" +
                "                   { \"from\": 40}\n" +
                "                ]\n" +
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

        RangeAggregation range = result.getAggregations().getRangeAggregation("range1");
        assertEquals("range1", range.getName());
        assertEquals(3, range.getBuckets().size());

        assertTrue(2L == range.getBuckets().get(0).getCount());
        assertNull(range.getBuckets().get(0).getFrom());
        assertEquals(Double.valueOf("20"), range.getBuckets().get(0).getTo());

        assertTrue(1L == range.getBuckets().get(1).getCount());
        assertEquals(Double.valueOf("40"), range.getBuckets().get(1).getTo());
        assertEquals(Double.valueOf("20"), range.getBuckets().get(1).getFrom());

        assertTrue(1L == range.getBuckets().get(2).getCount());
        assertNull(range.getBuckets().get(2).getTo());
        assertEquals(Double.valueOf("40"), range.getBuckets().get(2).getFrom());

        Aggregation aggregation = result.getAggregations().getAggregation("range1", RangeAggregation.class);
        assertTrue(aggregation instanceof RangeAggregation);
        RangeAggregation rangeByType = (RangeAggregation) aggregation;
        assertEquals(range, rangeByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("range1", RangeAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof RangeAggregation);
        RangeAggregation rangeWithMap = (RangeAggregation) aggregations.get(0);
        assertEquals(range, rangeWithMap);
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

        index(INDEX, TYPE, null, "{\"num\": 17}");
        index(INDEX, TYPE, null, "{\"num\":24}");
        index(INDEX, TYPE, null, "{\"num\":42}");
        index(INDEX, TYPE, null, "{\"num\":16}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"range1\" : {\n" +
                "            \"range\" : {\n" +
                "                \"field\" : \"bad_field\",\n" +
                "                \"ranges\" : [\n" +
                "                   { \"to\": 20},\n" +
                "                   { \"from\": 20, \"to\": 40},\n" +
                "                   { \"from\": 40}\n" +
                "                ]\n" +
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
        RangeAggregation range = result.getAggregations().getRangeAggregation("range1");
        assertEquals("range1", range.getName());
        assertEquals(3, range.getBuckets().size());

        assertTrue(0L == range.getBuckets().get(0).getCount());
        assertNull(range.getBuckets().get(0).getFrom());
        assertEquals(Double.valueOf("20"), range.getBuckets().get(0).getTo());

        assertTrue(0L == range.getBuckets().get(1).getCount());
        assertEquals(Double.valueOf("40"), range.getBuckets().get(1).getTo());
        assertEquals(Double.valueOf("20"), range.getBuckets().get(1).getFrom());

        assertTrue(0L == range.getBuckets().get(2).getCount());
        assertNull(range.getBuckets().get(2).getTo());
        assertEquals(Double.valueOf("40"), range.getBuckets().get(2).getFrom());

        Aggregation aggregation = result.getAggregations().getAggregation("range1", RangeAggregation.class);
        assertTrue(aggregation instanceof RangeAggregation);
        RangeAggregation rangeByType = (RangeAggregation) aggregation;
        assertEquals(range, rangeByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("range1", RangeAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof RangeAggregation);
        RangeAggregation rangeWithMap = (RangeAggregation) aggregations.get(0);
        assertEquals(range, rangeWithMap);
    }
}
