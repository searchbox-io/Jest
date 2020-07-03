package io.searchbox.core.search.aggregation;

import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;

import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cfstout
 */
@ESIntegTestCase.ClusterScope (scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class SignificantTermsAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String TYPE = "document";

    @Test
    public void testGetSignificantTermsAggregation()
            throws Exception {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                        .source("{\"document\":{\"properties\":{\"gender\":{\"store\":true,\"type\":\"keyword\"},"+
                                "\"favorite_movie\":{\"store\":true,\"type\":\"keyword\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());
        assertConcreteMappingsOnAll(INDEX, TYPE, "gender", "favorite_movie");

        index(INDEX, TYPE, null, "{\"gender\":\"male\", \"favorite_movie\": \"300\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"male\", \"favorite_movie\": \"300\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"male\", \"favorite_movie\": \"Toy Story\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"male\", \"favorite_movie\": \"Harry Potter\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"male\", \"favorite_movie\": \"Twilight\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\", \"favorite_movie\": \"Twilight\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\", \"favorite_movie\": \"Twilight\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\", \"favorite_movie\": \"Twilight\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\", \"favorite_movie\": \"The Notebook\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\", \"favorite_movie\": \"Titanic\"}");
        flushAndRefresh(INDEX);
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"terms\" : {\"gender\": [\"female\"]}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"significant_terms1\" : {\n" +
                "            \"significant_terms\" : {\n" +
                "                \"field\" : \"favorite_movie\"\n" +
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

        SignificantTermsAggregation significantTerms = result.getAggregations().getSignificantTermsAggregation("significant_terms1");
        assertEquals("significant_terms1", significantTerms.getName());
        assertTrue(5L == significantTerms.getTotalCount());
        assertEquals(1, significantTerms.getBuckets().size());

        SignificantTermsAggregation.SignificantTerm twilight = significantTerms.getBuckets().get(0);
        assertEquals("Twilight", twilight.getKey());
        assertNotEquals(Long.valueOf(0L), twilight.getCount());
        assertNotEquals(Long.valueOf(0L), twilight.getBackgroundCount());
        assertNotEquals(0, twilight.getScore());

        Aggregation aggregation = result.getAggregations().getAggregation("significant_terms1", SignificantTermsAggregation.class);
        assertTrue(aggregation instanceof SignificantTermsAggregation);
        SignificantTermsAggregation significantTermsByType = (SignificantTermsAggregation) aggregation;
        assertEquals(significantTerms, significantTermsByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("significant_terms1", SignificantTermsAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof SignificantTermsAggregation);
        SignificantTermsAggregation significantTermsWithMap = (SignificantTermsAggregation) aggregations.get(0);
        assertEquals(significantTermsWithMap, significantTermsByType);
    }

    @Test
    public void testBadAggregationQueryResult()
            throws Exception {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"gender\":{\"store\":true,\"type\":\"text\"}," +
                        "\"favorite_movie\":{\"store\":true,\"type\":\"text\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());
        assertConcreteMappingsOnAll(INDEX, TYPE, "gender", "favorite_movie");

        index(INDEX, TYPE, null, "{\"gender\":\"male\", \"favorite_movie\": \"300\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"male\", \"favorite_movie\": \"300\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"male\", \"favorite_movie\": \"Toy Story\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"male\", \"favorite_movie\": \"Harry Potter\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"male\", \"favorite_movie\": \"Twilight\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\", \"favorite_movie\": \"Twilight\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\", \"favorite_movie\": \"Twilight\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\", \"favorite_movie\": \"Twilight\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\", \"favorite_movie\": \"The Notebook\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\", \"favorite_movie\": \"Titanic\"}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"terms\" : {\"gender\": [\"female\"]}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"significant_terms1\" : {\n" +
                "            \"significant_terms\" : {\n" +
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
        SignificantTermsAggregation significantTerms = result.getAggregations().getSignificantTermsAggregation("significant_terms1");
        assertNull(significantTerms.getTotalCount());
        assertTrue(significantTerms.getBuckets().isEmpty());

        Aggregation aggregation = result.getAggregations().getAggregation("significant_terms1", SignificantTermsAggregation.class);
        assertTrue(aggregation instanceof SignificantTermsAggregation);
        SignificantTermsAggregation significantTermsByType = (SignificantTermsAggregation) aggregation;
        assertEquals(significantTerms, significantTermsByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("significant_terms1", SignificantTermsAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof SignificantTermsAggregation);
        SignificantTermsAggregation significantTermsWithMap = (SignificantTermsAggregation) aggregations.get(0);
        assertEquals(significantTermsWithMap, significantTermsByType);
    }
}
