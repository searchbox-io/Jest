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
public class GeoBoundsAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "geo_bounds_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGeoBoundsAggregation()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"location\":{\"store\":true,\"type\":\"geo_point\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 40.12,\"lon\" : -71.34},\"tag\" : [\"food\", \"family\"]}");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 38.53999991901219,\"lon\" : -71.78000008687377},\"tag\" : [\"gas\", \"food\"]}");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 41.229999996721745,\"lon\" : -70.67000007256866},\"tag\" : [\"gas\"]}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match\" : { \"tag\" : \"gas\" }\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"viewport\" : {\n" +
                "            \"geo_bounds\" : {\n" +
                "                \"field\" : \"location\"\n" +
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

        GeoBoundsAggregation geoBounds = result.getAggregations().getGeoBoundsAggregation("viewport");
        assertEquals("viewport", geoBounds.getName());
        assertEquals(new Double(38.53999991901219), geoBounds.getBottomRightLat());
        assertEquals(new Double(-70.67000007256866), geoBounds.getBottomRightLon());
        assertEquals(new Double(41.229999996721745), geoBounds.getTopLeftLat());
        assertEquals(new Double(-71.78000008687377), geoBounds.getTopLeftLon());

        Aggregation aggregation = result.getAggregations().getAggregation("viewport", GeoBoundsAggregation.class);
        assertTrue(aggregation instanceof GeoBoundsAggregation);
        GeoBoundsAggregation geoBoundsByType = (GeoBoundsAggregation) aggregation;
        assertEquals(geoBounds, geoBoundsByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("viewport", GeoBoundsAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof GeoBoundsAggregation);
        GeoBoundsAggregation geoBoundsWithMap = (GeoBoundsAggregation) aggregations.get(0);
        assertEquals(geoBounds, geoBoundsWithMap);
    }

    @Test
    public void testBadAggregationQueryResult()
            throws IOException {

        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"location\":{\"store\":true,\"type\":\"geo_point\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 40.12,\"lon\" : -71.34},\"tag\" : [\"food\", \"family\"]}");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 38.53999991901219,\"lon\" : -71.78000008687377},\"tag\" : [\"gas\", \"food\"]}");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 41.229999996721745,\"lon\" : -70.67000007256866},\"tag\" : [\"gas\"]}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match\" : { \"tag\" : \"gas\" }\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"viewport\" : {\n" +
                "            \"geo_bounds\" : {\n" +
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
        GeoBoundsAggregation geoBounds = result.getAggregations().getGeoBoundsAggregation("viewport");
        assertEquals("viewport", geoBounds.getName());
        assertNull(geoBounds.getBottomRightLat());
        assertNull(geoBounds.getBottomRightLon());
        assertNull(geoBounds.getTopLeftLat());
        assertNull(geoBounds.getTopLeftLon());

        Aggregation aggregation = result.getAggregations().getAggregation("viewport", GeoBoundsAggregation.class);
        assertTrue(aggregation instanceof GeoBoundsAggregation);
        GeoBoundsAggregation geoBoundsByType = (GeoBoundsAggregation) aggregation;
        assertEquals(geoBounds, geoBoundsByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("viewport", GeoBoundsAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof GeoBoundsAggregation);
        GeoBoundsAggregation geoBoundsWithMap = (GeoBoundsAggregation) aggregations.get(0);
        assertEquals(geoBounds, geoBoundsWithMap);
    }
}
