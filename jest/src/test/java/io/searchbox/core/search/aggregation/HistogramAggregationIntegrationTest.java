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
public class HistogramAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "histogram_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetHistogramAggregation()
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
                "        \"histo1\" : {\n" +
                "            \"histogram\" : {\n" +
                "                \"field\" : \"num\",\n" +
                "                \"interval\" : 20\n" +
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

        HistogramAggregation histogram = result.getAggregations().getHistogramAggregation("histo1");
        assertEquals("histo1", histogram.getName());
        assertEquals(3, histogram.getBuckets().size());

        assertTrue(2L == histogram.getBuckets().get(0).getCount());
        assertTrue(0L == histogram.getBuckets().get(0).getKey());

        assertTrue(1L == histogram.getBuckets().get(1).getCount());
        assertTrue(20L == histogram.getBuckets().get(1).getKey());

        assertTrue(1L == histogram.getBuckets().get(2).getCount());
        assertTrue(40L == histogram.getBuckets().get(2).getKey());

        Aggregation aggregation = result.getAggregations().getAggregation("histo1", HistogramAggregation.class);
        assertTrue(aggregation instanceof HistogramAggregation);
        HistogramAggregation histogramByType = (HistogramAggregation) aggregation;
        assertEquals(histogram, histogramByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("histo1", HistogramAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof HistogramAggregation);
        HistogramAggregation histogramWithMap = (HistogramAggregation) aggregations.get(0);
        assertEquals(histogram, histogramWithMap);
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
                "        \"histo1\" : {\n" +
                "            \"histogram\" : {\n" +
                "                \"field\" : \"bad_field\",\n" +
                "                \"interval\" : 20\n" +
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
        HistogramAggregation histogram = result.getAggregations().getHistogramAggregation("histo1");
        assertTrue(histogram.getBuckets().isEmpty());

        Aggregation aggregation = result.getAggregations().getAggregation("histo1", HistogramAggregation.class);
        assertTrue(aggregation instanceof HistogramAggregation);
        HistogramAggregation histogramByType = (HistogramAggregation) aggregation;
        assertEquals(histogram, histogramByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("histo1", HistogramAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof HistogramAggregation);
        HistogramAggregation histogramWithMap = (HistogramAggregation) aggregations.get(0);
        assertEquals(histogram, histogramWithMap);
    }
}
