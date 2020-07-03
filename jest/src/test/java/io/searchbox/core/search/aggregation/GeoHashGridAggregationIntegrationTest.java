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
public class GeoHashGridAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "geohash_grid_aggregation";
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
                "        \"grid\" : {\n" +
                "            \"geohash_grid\" : {\n" +
                "                \"field\" : \"location\",\n" +
                "                \"precision\" : 5\n" +
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

        GeoHashGridAggregation geohashGrid = result.getAggregations().getGeohashGridAggregation("grid");
        assertEquals("grid", geohashGrid.getName());
        assertEquals(3, geohashGrid.getBuckets().size());

        assertTrue(3L == geohashGrid.getBuckets().get(0).getCount());
        assertEquals("9v6kw", geohashGrid.getBuckets().get(0).getKey());

        assertTrue(1L == geohashGrid.getBuckets().get(1).getCount());
        assertEquals("9v6kz", geohashGrid.getBuckets().get(1).getKey());

        assertTrue(1L == geohashGrid.getBuckets().get(2).getCount());
        assertEquals("9v6kp", geohashGrid.getBuckets().get(2).getKey());

        Aggregation aggregation = result.getAggregations().getAggregation("grid", GeoHashGridAggregation.class);
        assertTrue(aggregation instanceof GeoHashGridAggregation);
        GeoHashGridAggregation geohashGridByName = (GeoHashGridAggregation) aggregation;
        assertEquals(geohashGrid, geohashGridByName);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("grid", GeoHashGridAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof GeoHashGridAggregation);
        GeoHashGridAggregation geohashGridWithMap = (GeoHashGridAggregation) aggregations.get(0);
        assertEquals(geohashGrid, geohashGridWithMap);
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
                "        \"grid\" : {\n" +
                "            \"geohash_grid\" : {\n" +
                "                \"field\" : \"bad_field\",\n" +
                "                \"precision\" : 5\n" +
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

        GeoHashGridAggregation geohashGrid = result.getAggregations().getGeohashGridAggregation("grid");
        assertEquals("grid", geohashGrid.getName());
        assertTrue(geohashGrid.getBuckets().isEmpty());

        Aggregation aggregation = result.getAggregations().getAggregation("grid", GeoHashGridAggregation.class);
        assertTrue(aggregation instanceof GeoHashGridAggregation);
        GeoHashGridAggregation geohashGridByName = (GeoHashGridAggregation) aggregation;
        assertEquals(geohashGrid, geohashGridByName);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("grid", GeoHashGridAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof GeoHashGridAggregation);
        GeoHashGridAggregation geohashGridWithMap = (GeoHashGridAggregation) aggregations.get(0);
        assertEquals(geohashGrid, geohashGridWithMap);
    }
}
