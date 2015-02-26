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
public class SignificantTermsAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "significant_terms_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetSignificantTermsAggregation()
            throws Exception {
        createIndex(INDEX);
        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                        .source("{\"document\":{\"properties\":{\"gender\":{\"store\":true,\"type\":\"string\"},"+
                         "\"favorite_movie\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();

        assertTrue(putMappingResponse.isAcknowledged());
        waitForConcreteMappingsOnAll(INDEX, TYPE, "gender", "favorite_movie");

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
        assertEquals(1, significantTerms.getSignificantTerms().size());
        assertEquals("twilight", significantTerms.getSignificantTerms().get(0).getKey());
        assertTrue(3L == significantTerms.getSignificantTerms().get(0).getCount());
        assertEquals(new Double(0.2999999999999999), significantTerms.getSignificantTerms().get(0).getScore());
        assertTrue(4L == significantTerms.getSignificantTerms().get(0).getBackgroundCount());

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
        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                        .source("{\"document\":{\"properties\":{\"gender\":{\"store\":true,\"type\":\"string\"},"+
                                "\"favorite_movie\":{\"store\":true,\"type\":\"string\"}}}}")
        ).actionGet();

        assertTrue(putMappingResponse.isAcknowledged());
        waitForConcreteMappingsOnAll(INDEX, TYPE, "gender", "favorite_movie");

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
        assertTrue(significantTerms.getSignificantTerms().isEmpty());

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
