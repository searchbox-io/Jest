package io.searchbox.core.search.aggregation;

import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESIntegTestCase.ClusterScope;
import org.elasticsearch.test.ESIntegTestCase.Scope;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cfstout
 */
@ClusterScope(scope = Scope.TEST, numDataNodes = 1)
public class CardinalityAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "cardinality_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetCardinalityAggregation()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"doc_id\":{\"store\":true,\"type\":\"keyword\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"doc_id\":\"abc\"}");
        index(INDEX, TYPE, null, "{\"doc_id\":\"def\"}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"card1\" : {\n" +
                "            \"cardinality\" : {\n" +
                "                \"field\" : \"doc_id\"\n" +
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

        CardinalityAggregation cardinality = result.getAggregations().getCardinalityAggregation("card1");
        assertEquals("card1", cardinality.getName());
        assertEquals(new Long(2), cardinality.getCardinality());

        Aggregation aggregation = result.getAggregations().getAggregation("card1", CardinalityAggregation.class);
        assertTrue(aggregation instanceof CardinalityAggregation);
        CardinalityAggregation cardinalityByType = (CardinalityAggregation) aggregation;
        assertEquals(cardinality, cardinalityByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("card1", CardinalityAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof CardinalityAggregation);
        CardinalityAggregation cardinalityWithMap = (CardinalityAggregation) aggregations.get(0);
        assertEquals(cardinalityWithMap, cardinalityByType);
    }

    @Test
    public void testBadAggregationQueryResult()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"doc_id\":{\"store\":true,\"type\":\"keyword\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"doc_id\":\"abc\"}");
        index(INDEX, TYPE, null, "{\"doc_id\":\"def\"}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"card1\" : {\n" +
                "            \"cardinality\" : {\n" +
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
        CardinalityAggregation cardinality = result.getAggregations().getCardinalityAggregation("card1");
        assertTrue(0L == cardinality.getCardinality());

        Aggregation aggregation = result.getAggregations().getAggregation("card1", CardinalityAggregation.class);
        assertTrue(aggregation instanceof CardinalityAggregation);
        CardinalityAggregation cardinalityByType = (CardinalityAggregation) aggregation;
        assertEquals(cardinality, cardinalityByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("card1", CardinalityAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof CardinalityAggregation);
        CardinalityAggregation cardinalityWithMap = (CardinalityAggregation) aggregations.get(0);
        assertEquals(cardinalityWithMap, cardinalityByType);
    }
}
