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
public class DateRangeAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "date_range_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetDateRangeAggregation()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"delivery\":{\"store\":true,\"type\":\"date\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"delivery\":\"2013-02-04\"}");
        index(INDEX, TYPE, null, "{\"delivery\":\"2013-02-04\"}");
        index(INDEX, TYPE, null, "{\"delivery\":\"2013-02-01\"}");
        index(INDEX, TYPE, null, "{\"delivery\":\"2013-02-03\"}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"date_range1\" : {\n" +
                "            \"date_range\" : {\n" +
                "                \"field\" : \"delivery\",\n" +
                "                \"format\" : \"yyy-MM-dd\",\n" +
                "                \"ranges\" : [\n" +
                "                   { \"to\": \"2013-02-03||/d+1m\"},\n" +
                "                   { \"from\": \"2013-02-03||/d+1m\"}\n" +
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

        DateRangeAggregation dateRange = result.getAggregations().getDateRangeAggregation("date_range1");
        assertEquals("date_range1", dateRange.getName());
        assertEquals(2, dateRange.getBuckets().size());

        assertTrue(2L == dateRange.getBuckets().get(0).getCount());
        assertNull(dateRange.getBuckets().get(0).getFrom());
        assertNull(dateRange.getBuckets().get(0).getFromAsString());
        assertEquals(Double.valueOf("1.35984966E12"), dateRange.getBuckets().get(0).getTo());
        assertEquals("2013-02-03", dateRange.getBuckets().get(0).getToAsString());

        assertTrue(2L == dateRange.getBuckets().get(1).getCount());
        assertNull(dateRange.getBuckets().get(1).getTo());
        assertNull(dateRange.getBuckets().get(1).getToAsString());
        assertEquals(Double.valueOf("1.35984966E12"), dateRange.getBuckets().get(1).getFrom());
        assertEquals("2013-02-03", dateRange.getBuckets().get(1).getFromAsString());

        Aggregation aggregation = result.getAggregations().getAggregation("date_range1", DateRangeAggregation.class);
        assertTrue(aggregation instanceof DateRangeAggregation);
        DateRangeAggregation dateRangeByType = (DateRangeAggregation) aggregation;
        assertEquals(dateRange, dateRangeByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("date_range1", DateRangeAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof DateRangeAggregation);
        DateRangeAggregation dateRangeWithMap = (DateRangeAggregation) aggregations.get(0);
        assertEquals(dateRange, dateRangeWithMap);
    }

    @Test
    public void testBadAggregationQueryResult()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"delivery\":{\"store\":true,\"type\":\"date\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"delivery\":\"2013-02-04\"}");
        index(INDEX, TYPE, null, "{\"delivery\":\"2013-02-04\"}");
        index(INDEX, TYPE, null, "{\"delivery\":\"2013-02-01\"}");
        index(INDEX, TYPE, null, "{\"delivery\":\"2013-02-03\"}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"date_range1\" : {\n" +
                "            \"date_range\" : {\n" +
                "                \"field\" : \"bad_field\",\n" +
                "                \"format\" : \"yyy-MM-dd\",\n" +
                "                \"ranges\" : [\n" +
                "                   { \"to\": \"2013-02-03||/d+1m\"},\n" +
                "                   { \"from\": \"2013-02-03||/d+1m\"}\n" +
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
        DateRangeAggregation dateRange = result.getAggregations().getDateRangeAggregation("date_range1");
        assertEquals("date_range1", dateRange.getName());
        assertEquals(2, dateRange.getBuckets().size());

        assertTrue(0L == dateRange.getBuckets().get(0).getCount());
        assertNull(dateRange.getBuckets().get(0).getFrom());
        assertNull(dateRange.getBuckets().get(0).getFromAsString());
        assertEquals(Double.valueOf("1.35984966E12"), dateRange.getBuckets().get(0).getTo());
        assertEquals("2013-02-03T00:01:00.000Z", dateRange.getBuckets().get(0).getToAsString());

        assertTrue(0L == dateRange.getBuckets().get(1).getCount());
        assertNull(dateRange.getBuckets().get(1).getTo());
        assertNull(dateRange.getBuckets().get(1).getToAsString());
        assertEquals(Double.valueOf("1.35984966E12"), dateRange.getBuckets().get(1).getFrom());
        assertEquals("2013-02-03T00:01:00.000Z", dateRange.getBuckets().get(1).getFromAsString());

        Aggregation aggregation = result.getAggregations().getAggregation("date_range1", DateRangeAggregation.class);
        assertTrue(aggregation instanceof DateRangeAggregation);
        DateRangeAggregation dateRangeByType = (DateRangeAggregation) aggregation;
        assertEquals(dateRange, dateRangeByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("date_range1", DateRangeAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof DateRangeAggregation);
        DateRangeAggregation dateRangeWithMap = (DateRangeAggregation) aggregations.get(0);
        assertEquals(dateRange, dateRangeWithMap);
    }
}
