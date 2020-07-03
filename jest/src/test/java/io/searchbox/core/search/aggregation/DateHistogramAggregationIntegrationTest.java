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
public class DateHistogramAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "date_histogram_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetDateHistogramAggregation()
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
                "        \"histo1\" : {\n" +
                "            \"date_histogram\" : {\n" +
                "                \"field\" : \"delivery\",\n" +
                "                \"interval\" : \"day\",\n" +
                "                \"min_doc_count\": 1\n" +
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

        DateHistogramAggregation dateHistogram = result.getAggregations().getDateHistogramAggregation("histo1");
        assertEquals("histo1", dateHistogram.getName());
        assertEquals(3, dateHistogram.getBuckets().size());
        assertTrue(1L == dateHistogram.getBuckets().get(0).getCount());
        assertTrue(1359676800000L == dateHistogram.getBuckets().get(0).getTime());
        assertTrue(1L == dateHistogram.getBuckets().get(1).getCount());
        assertTrue(1359849600000L == dateHistogram.getBuckets().get(1).getTime());
        assertTrue(2L == dateHistogram.getBuckets().get(2).getCount());
        assertTrue(1359936000000L == dateHistogram.getBuckets().get(2).getTime());

        Aggregation aggregation = result.getAggregations().getAggregation("histo1", DateHistogramAggregation.class);
        assertTrue(aggregation instanceof DateHistogramAggregation);
        DateHistogramAggregation dateHistogramByType = (DateHistogramAggregation) aggregation;
        assertEquals(dateHistogram, dateHistogramByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("histo1", DateHistogramAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof DateHistogramAggregation);
        DateHistogramAggregation dateHistogramWithMap = (DateHistogramAggregation) aggregations.get(0);
        assertEquals(dateHistogram, dateHistogramWithMap);
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
                "        \"histo1\" : {\n" +
                "            \"date_histogram\" : {\n" +
                "                \"field\" : \"bad_field\",\n" +
                "                \"interval\" : \"day\"\n" +
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
        DateHistogramAggregation dateHistogram = result.getAggregations().getDateHistogramAggregation("histo1");
        assertTrue(dateHistogram.getBuckets().isEmpty());

        Aggregation aggregation = result.getAggregations().getAggregation("histo1", DateHistogramAggregation.class);
        assertTrue(aggregation instanceof DateHistogramAggregation);
        DateHistogramAggregation dateHistogramByType = (DateHistogramAggregation) aggregation;
        assertEquals(dateHistogram, dateHistogramByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("histo1", DateHistogramAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof DateHistogramAggregation);
        DateHistogramAggregation dateHistogramWithMap = (DateHistogramAggregation) aggregations.get(0);
        assertEquals(dateHistogram, dateHistogramWithMap);
    }
}
