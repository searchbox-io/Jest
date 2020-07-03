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
public class MissingAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "missing_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetMissingAggregation()
            throws IOException {
        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"num\":{\"store\":true,\"type\":\"integer\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"num\":2}");
        index(INDEX, TYPE, null, "{\"num\":3}");
        index(INDEX, TYPE, null, "{\"other\":4}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"missing1\" : {\n" +
                "            \"missing\" : {\n" +
                "                \"field\" : \"num\"\n" +
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

        MissingAggregation missing = result.getAggregations().getMissingAggregation("missing1");
        assertEquals("missing1", missing.getName());
        assertTrue(1L == missing.getMissing());

        Aggregation aggregation = result.getAggregations().getAggregation("missing1", MissingAggregation.class);
        assertTrue(aggregation instanceof MissingAggregation);
        MissingAggregation missingByType = (MissingAggregation) aggregation;
        assertEquals(missing, missingByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("missing1", MissingAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof MissingAggregation);
        MissingAggregation missingWithMap = (MissingAggregation) aggregations.get(0);
        assertEquals(missing, missingWithMap);
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

        index(INDEX, TYPE, null, "{\"num\":2}");
        index(INDEX, TYPE, null, "{\"num\":3}");
        index(INDEX, TYPE, null, "{\"other\":4}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"missing1\" : {\n" +
                "            \"missing\" : {\n" +
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
        MissingAggregation missing = result.getAggregations().getMissingAggregation("missing1");
        assertTrue(3L == missing.getMissing());

        Aggregation aggregation = result.getAggregations().getAggregation("missing1", MissingAggregation.class);
        assertTrue(aggregation instanceof MissingAggregation);
        MissingAggregation missingByType = (MissingAggregation) aggregation;
        assertEquals(missing, missingByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("missing1", MissingAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof MissingAggregation);
        MissingAggregation missingWithMap = (MissingAggregation) aggregations.get(0);
        assertEquals(missing, missingWithMap);
    }
}
