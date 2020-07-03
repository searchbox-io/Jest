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
public class GeoDistanceAggregationTest extends AbstractIntegrationTest {

    private final String INDEX = "geo_distance_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetGeoDistanceAggregationWithProperName()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"location\":{\"store\":true,\"type\":\"geo_point\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 30.337991,\"lon\" : -97.807305},\"tag\" : [\"food\", \"family\"]}");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 30.337991,\"lon\" : -97.807305},\"tag\" : [\"food\", \"family\"]}");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 30.337991,\"lon\" : -97.807305},\"tag\" : [\"food\", \"family\"]}");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 30.400026, \"lon\" : -97.737343 } }");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 30.257877, \"lon\" : -97.738726 } }");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"rings\" : {\n" +
                "            \"geo_distance\" : {\n" +
                "                \"field\" : \"location\",\n" +
                "                \"origin\" : {\n" +
                "                    \"lat\": 30.274707,\n" +
                "                    \"lon\": -97.740527\n" +
                "                },\n" +
                "                \"unit\" : \"mi\",\n" +
                "                \"ranges\" : [\n" +
                "                   { \"to\" : 5 }, \n" +
                "                   { \"from\" : 5, \"to\" : 7 }, \n" +
                "                   { \"from\" : 7} \n" +
                "                ]\n " +
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

        GeoDistanceAggregation geoDistance = result.getAggregations().getGeoDistanceAggregation("rings");
        assertEquals("rings", geoDistance.getName());
        assertEquals(3, geoDistance.getBuckets().size());

        assertTrue(1L == geoDistance.getBuckets().get(0).getCount());
        assertEquals(new Double(5), geoDistance.getBuckets().get(0).getTo());
        assertEquals(new Double(0), geoDistance.getBuckets().get(0).getFrom());

        assertTrue(3L == geoDistance.getBuckets().get(1).getCount());
        assertEquals(new Double(5), geoDistance.getBuckets().get(1).getFrom());
        assertEquals(new Double(7), geoDistance.getBuckets().get(1).getTo());

        assertTrue(1L == geoDistance.getBuckets().get(2).getCount());
        assertEquals(new Double(7), geoDistance.getBuckets().get(2).getFrom());
        assertNull(geoDistance.getBuckets().get(2).getTo());

        Aggregation aggregation = result.getAggregations().getAggregation("rings", GeoDistanceAggregation.class);
        assertTrue(aggregation instanceof GeoDistanceAggregation);
        GeoDistanceAggregation geoDistanceByName = (GeoDistanceAggregation) aggregation;
        assertEquals(geoDistance, geoDistanceByName);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("rings", GeoDistanceAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof GeoDistanceAggregation);
        GeoDistanceAggregation geoDistanceWithMap = (GeoDistanceAggregation) aggregations.get(0);
        assertEquals(geoDistance, geoDistanceWithMap);
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

        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 30.337991,\"lon\" : -97.807305},\"tag\" : [\"food\", \"family\"]}");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 30.337991,\"lon\" : -97.807305},\"tag\" : [\"food\", \"family\"]}");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 30.337991,\"lon\" : -97.807305},\"tag\" : [\"food\", \"family\"]}");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 30.400026, \"lon\" : -97.737343 } }");
        index(INDEX, TYPE, null, "{\"location\" : {\"lat\" : 30.257877, \"lon\" : -97.738726 } }");
        refresh();
        ensureSearchable(INDEX);


        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"rings\" : {\n" +
                "            \"geo_distance\" : {\n" +
                "                \"field\" : \"bad_field\",\n" +
                "                \"origin\" : {\n" +
                "                    \"lat\": 30.274707,\n" +
                "                    \"lon\": -97.740527\n" +
                "                },\n" +
                "                \"unit\" : \"mi\",\n" +
                "                \"ranges\" : [\n" +
                "                   { \"to\" : 5 }, \n" +
                "                   { \"from\" : 5, \"to\" : 7 }, \n" +
                "                   { \"from\" : 7} \n" +
                "                ]\n " +
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

        GeoDistanceAggregation geoDistance = result.getAggregations().getGeoDistanceAggregation("rings");
        assertEquals("rings", geoDistance.getName());
        assertEquals(3, geoDistance.getBuckets().size());

        assertTrue(0L == geoDistance.getBuckets().get(0).getCount());
        assertEquals(new Double(5), geoDistance.getBuckets().get(0).getTo());
        assertEquals(new Double(0), geoDistance.getBuckets().get(0).getFrom());

        assertTrue(0L == geoDistance.getBuckets().get(1).getCount());
        assertEquals(new Double(5), geoDistance.getBuckets().get(1).getFrom());
        assertEquals(new Double(7), geoDistance.getBuckets().get(1).getTo());

        assertTrue(0L == geoDistance.getBuckets().get(2).getCount());
        assertEquals(new Double(7), geoDistance.getBuckets().get(2).getFrom());
        assertNull(geoDistance.getBuckets().get(2).getTo());

        Aggregation aggregation = result.getAggregations().getAggregation("rings", GeoDistanceAggregation.class);
        assertTrue(aggregation instanceof GeoDistanceAggregation);
        GeoDistanceAggregation geoDistanceByName = (GeoDistanceAggregation) aggregation;
        assertEquals(geoDistance, geoDistanceByName);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("rings", GeoDistanceAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof GeoDistanceAggregation);
        GeoDistanceAggregation geoDistanceWithMap = (GeoDistanceAggregation) aggregations.get(0);
        assertEquals(geoDistance, geoDistanceWithMap);
    }
}
