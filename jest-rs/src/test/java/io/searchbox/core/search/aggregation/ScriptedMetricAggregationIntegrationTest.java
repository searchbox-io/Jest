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
public class ScriptedMetricAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "scripted_metric_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetScriptedMetricAggregation()
            throws IOException {
        createIndex(INDEX);
        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                        .source("{\"document\":{\"properties\":{\"amount\":{\"store\":true,\"type\":\"integer\"}}}}")
        ).actionGet();

        assertTrue(putMappingResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"amount\":2}");
        index(INDEX, TYPE, null, "{\"amount\":3}");
        index(INDEX, TYPE, null, "{\"amount\":-1}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"profit\" : {\n" +
                "            \"scripted_metric\" : {\n" +
                "                \"init_script\" : \"_agg['transactions'] = []\",\n" +
                "                \"map_script\" : \"_agg.transactions.add(doc['amount'].value)\",\n" +
                "                \"combine_script\" : \"profit = 0; for (t in _agg.transactions) { profit += t }; return profit\",\n" +
                "                \"reduce_script\" : \"profit = 0; for (a in _aggs) { profit += a }; return profit\"\n"+
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

        ScriptedMetricAggregation scriptedMetric = result.getAggregations().getScriptedMetricAggregation("profit");
        assertEquals("profit", scriptedMetric.getName());
        assertEquals(new Double(4) , scriptedMetric.getScriptedMetric());

        Aggregation aggregation = result.getAggregations().getAggregation("profit", ScriptedMetricAggregation.class);
        assertTrue(aggregation instanceof ScriptedMetricAggregation);
        ScriptedMetricAggregation scriptedMetricByType = (ScriptedMetricAggregation) aggregation;
        assertEquals(scriptedMetric, scriptedMetricByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("profit", ScriptedMetricAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof ScriptedMetricAggregation);
        ScriptedMetricAggregation scriptedMetricWithMap = (ScriptedMetricAggregation) aggregations.get(0);
        assertEquals(scriptedMetric, scriptedMetricWithMap);
    }

    @Test
    public void testBadAggregationQueryResult()
            throws Exception {
        createIndex(INDEX);
        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                        .source("{\"document\":{\"properties\":{\"amount\":{\"store\":true,\"type\":\"integer\"},"+
                        "\"bad_field\":{\"store\":true,\"type\":\"integer\"}}}}")
        ).actionGet();

        assertTrue(putMappingResponse.isAcknowledged());
        waitForConcreteMappingsOnAll(INDEX, TYPE, "amount", "bad_field");

        index(INDEX, TYPE, null, "{\"amount\":2}");
        index(INDEX, TYPE, null, "{\"amount\":3}");
        index(INDEX, TYPE, null, "{\"amount\":-1}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"profit\" : {\n" +
                "            \"scripted_metric\" : {\n" +
                "                \"init_script\" : \"_agg['transactions'] = []\",\n" +
                "                \"map_script\" : \"_agg.transactions.add(doc['bad_field'].value)\",\n" +
                "                \"combine_script\" : \"profit = 0; for (t in _agg.transactions) { profit += t }; return profit\",\n" +
                "                \"reduce_script\" : \"profit = 0; for (a in _aggs) { profit += a }; return profit\"\n"+
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
        ScriptedMetricAggregation scriptedMetric = result.getAggregations().getScriptedMetricAggregation("profit");
        assertEquals(new Double(0), scriptedMetric.getScriptedMetric());

        Aggregation aggregation = result.getAggregations().getAggregation("profit", ScriptedMetricAggregation.class);
        assertTrue(aggregation instanceof ScriptedMetricAggregation);
        ScriptedMetricAggregation scriptedMetricByType = (ScriptedMetricAggregation) aggregation;
        assertEquals(scriptedMetric, scriptedMetricByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("profit", ScriptedMetricAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof ScriptedMetricAggregation);
        ScriptedMetricAggregation scriptedMetricWithMap = (ScriptedMetricAggregation) aggregations.get(0);
        assertEquals(scriptedMetric, scriptedMetricWithMap);
    }
}
