package io.searchbox.core.search.aggregation;

import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cfstout
 */
@ElasticsearchIntegrationTest.ClusterScope (scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 1)
public class Ipv4RangeAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "ipv4_range_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetIpv4RangRangeAggregation()
            throws IOException {
        createIndex(INDEX);
        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                        .source("{\"document\":{\"properties\":{\"address\":{\"store\":true,\"type\":\"ip\"}}}}")
        ).actionGet();

        assertTrue(putMappingResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"address\":\"10.0.0.23\"}");
        index(INDEX, TYPE, null, "{\"address\":\"10.0.0.1\"}");
        index(INDEX, TYPE, null, "{\"address\":\"10.0.1.0\"}");
        index(INDEX, TYPE, null, "{\"address\":\"10.0.0.123\"}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"ipv4_range1\" : {\n" +
                "            \"ip_range\" : {\n" +
                "                \"field\" : \"address\",\n" +
                "                \"ranges\" : [\n" +
                "                   { \"to\": \"10.0.0.25\"},\n" +
                "                   { \"from\": \"10.0.0.25\"}\n" +
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

        Ipv4RangeAggregation ipv4Range = result.getAggregations().getIpv4RangeAggregation("ipv4_range1");
        assertEquals("ipv4_range1", ipv4Range.getName());
        assertEquals(2, ipv4Range.getBuckets().size());

        assertTrue(2L == ipv4Range.getBuckets().get(0).getCount());
        assertNull(ipv4Range.getBuckets().get(0).getFrom());
        assertNull(ipv4Range.getBuckets().get(0).getFromAsString());
        assertEquals(Double.valueOf("1.67772185E8"), ipv4Range.getBuckets().get(0).getTo());
        assertEquals("10.0.0.25", ipv4Range.getBuckets().get(0).getToAsString());

        assertTrue(2L == ipv4Range.getBuckets().get(1).getCount());
        assertNull(ipv4Range.getBuckets().get(1).getTo());
        assertNull(ipv4Range.getBuckets().get(1).getToAsString());
        assertEquals(Double.valueOf("1.67772185E8"), ipv4Range.getBuckets().get(1).getFrom());
        assertEquals("10.0.0.25", ipv4Range.getBuckets().get(1).getFromAsString());

        Aggregation aggregation = result.getAggregations().getAggregation("ipv4_range1", Ipv4RangeAggregation.class);
        assertTrue(aggregation instanceof Ipv4RangeAggregation);
        Ipv4RangeAggregation ipv4RangeByType = (Ipv4RangeAggregation) aggregation;
        assertEquals(ipv4Range, ipv4RangeByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("ipv4_range1", Ipv4RangeAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof Ipv4RangeAggregation);
        Ipv4RangeAggregation ipv4RangeWithMap = (Ipv4RangeAggregation) aggregations.get(0);
        assertEquals(ipv4Range, ipv4RangeWithMap);
    }

    @Test
    public void testBadAggregationQueryResult()
            throws IOException {
        createIndex(INDEX);
        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                        .source("{\"document\":{\"properties\":{\"address\":{\"store\":true,\"type\":\"ip\"}}}}")
        ).actionGet();

        assertTrue(putMappingResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"address\":\"10.0.0.23\"}");
        index(INDEX, TYPE, null, "{\"address\":\"10.0.0.1\"}");
        index(INDEX, TYPE, null, "{\"address\":\"10.0.1.0\"}");
        index(INDEX, TYPE, null, "{\"address\":\"10.0.0.123\"}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"ipv4_range1\" : {\n" +
                "            \"ip_range\" : {\n" +
                "                \"field\" : \"bad_field\",\n" +
                "                \"ranges\" : [\n" +
                "                   { \"to\": \"10.0.0.25\"},\n" +
                "                   { \"from\": \"10.0.0.25\"}\n" +
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
        Ipv4RangeAggregation ipv4Range = result.getAggregations().getIpv4RangeAggregation("ipv4_range1");
        assertEquals("ipv4_range1", ipv4Range.getName());
        assertEquals(2, ipv4Range.getBuckets().size());

        assertTrue(0L == ipv4Range.getBuckets().get(0).getCount());
        assertNull(ipv4Range.getBuckets().get(0).getFrom());
        assertNull(ipv4Range.getBuckets().get(0).getFromAsString());
        assertEquals(Double.valueOf("1.67772185E8"), ipv4Range.getBuckets().get(0).getTo());
        assertEquals("10.0.0.25", ipv4Range.getBuckets().get(0).getToAsString());

        assertTrue(0L == ipv4Range.getBuckets().get(1).getCount());
        assertNull(ipv4Range.getBuckets().get(1).getTo());
        assertNull(ipv4Range.getBuckets().get(1).getToAsString());
        assertEquals(Double.valueOf("1.67772185E8"), ipv4Range.getBuckets().get(1).getFrom());
        assertEquals("10.0.0.25", ipv4Range.getBuckets().get(1).getFromAsString());

        Aggregation aggregation = result.getAggregations().getAggregation("ipv4_range1", Ipv4RangeAggregation.class);
        assertTrue(aggregation instanceof Ipv4RangeAggregation);
        Ipv4RangeAggregation ipv4RangeByType = (Ipv4RangeAggregation) aggregation;
        assertEquals(ipv4Range, ipv4RangeByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("ipv4_range1", Ipv4RangeAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof Ipv4RangeAggregation);
        Ipv4RangeAggregation ipv4RangeWithMap = (Ipv4RangeAggregation) aggregations.get(0);
        assertEquals(ipv4Range, ipv4RangeWithMap);
    }
}
