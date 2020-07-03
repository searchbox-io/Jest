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
public class TermsAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "terms_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetTermsAggregation()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"gender\":{\"store\":true,\"type\":\"keyword\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"gender\":\"male\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"male\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\"}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"terms1\" : {\n" +
                "            \"terms\" : {\n" +
                "                \"field\" : \"gender\"\n" +
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

        TermsAggregation terms = result.getAggregations().getTermsAggregation("terms1");
        assertEquals("terms1", terms.getName());
        assertTrue(0L == terms.getDocCountErrorUpperBound());
        assertTrue(0L == terms.getSumOtherDocCount());
        assertEquals(2, terms.getBuckets().size());
        assertTrue(2L == terms.getBuckets().get(0).getCount());
        assertEquals("male", terms.getBuckets().get(0).getKey());
        assertTrue(1L == terms.getBuckets().get(1).getCount());
        assertEquals("female", terms.getBuckets().get(1).getKey());

        Aggregation aggregation = result.getAggregations().getAggregation("terms1", TermsAggregation.class);
        assertTrue(aggregation instanceof TermsAggregation);
        TermsAggregation termsByType = (TermsAggregation) aggregation;
        assertEquals(terms, termsByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("terms1", TermsAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof TermsAggregation);
        TermsAggregation termsWithMap = (TermsAggregation) aggregations.get(0);
        assertEquals(termsWithMap, termsByType);
    }

    @Test
    public void testBadAggregationQueryResult()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"gender\":{\"store\":true,\"type\":\"text\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"gender\":\"male\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"male\"}");
        index(INDEX, TYPE, null, "{\"gender\":\"female\"}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"terms1\" : {\n" +
                "            \"terms\" : {\n" +
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
        TermsAggregation terms = result.getAggregations().getTermsAggregation("terms1");
        assertEquals(0, terms.getBuckets().size());
        assertTrue(0L == terms.getDocCountErrorUpperBound());
        assertTrue(0L == terms.getSumOtherDocCount());

        Aggregation aggregation = result.getAggregations().getAggregation("terms1", TermsAggregation.class);
        assertTrue(aggregation instanceof TermsAggregation);
        TermsAggregation termsByType = (TermsAggregation) aggregation;
        assertEquals(terms, termsByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("terms1", TermsAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof TermsAggregation);
        TermsAggregation termsWithMap = (TermsAggregation) aggregations.get(0);
        assertEquals(termsWithMap, termsByType);
    }
}
