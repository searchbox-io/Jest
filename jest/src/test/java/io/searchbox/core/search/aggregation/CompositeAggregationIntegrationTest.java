package io.searchbox.core.search.aggregation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

@ESIntegTestCase.ClusterScope (scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class CompositeAggregationIntegrationTest extends AbstractIntegrationTest {

    public static final Gson gson = new Gson();
    private final String INDEX = "composite_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetCompositeAggregation()
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
        final JsonObject afterKey;
        {
            String query = "{\n" +
                    "    \"query\" : {\n" +
                    "        \"match_all\" : {}\n" +
                    "    },\n" +
                    "    \"aggs\" : {\n" +
                    "        \"composite1\" : {\n" +
                    "            \"composite\" : {\n" +
                    "            \"size\" : 10,\n" +
                    "                \"sources\":[{\"term1\": { \"terms\":{\"field\" : \"gender\"}}}]\n" +
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

            CompositeAggregation composite = result.getAggregations().getCompositeAggregation("composite1");
            assertEquals("composite1", composite.getName());
            assertEquals(2, composite.getBuckets().size());
            assertTrue(2L == composite.getBuckets().get(1).getCount());
            assertEquals(gson.fromJson("{\"term1\":\"male\"}", JsonObject.class), composite.getBuckets().get(1).getKey());
            assertTrue(1L == composite.getBuckets().get(0).getCount());
            assertEquals(gson.fromJson("{\"term1\":\"female\"}", JsonObject.class), composite.getBuckets().get(0).getKey());

            Aggregation aggregation = result.getAggregations().getAggregation("composite1", CompositeAggregation.class);
            assertTrue(aggregation instanceof CompositeAggregation);
            CompositeAggregation compositeByType = (CompositeAggregation) aggregation;
            assertEquals(composite, compositeByType);

            Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
            nameToTypeMap.put("composite1", CompositeAggregation.class);
            List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
            assertEquals(1, aggregations.size());
            assertTrue(aggregations.get(0) instanceof CompositeAggregation);
            CompositeAggregation termsWithMap = (CompositeAggregation) aggregations.get(0);
            assertEquals(termsWithMap, compositeByType);

            afterKey = composite.getAfterKey();
            assertNotNull("ain't sure why", afterKey);
        }

        String query2 = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"composite1\" : {\n" +
                "            \"composite\" : {\n" +
                "            \"after\" : "+ gson.toJson(afterKey)+",\n" +
                "                \"sources\":[{\"term1\": { \"terms\":{\"field\" : \"gender\"}}}]\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        Search search2 = new Search.Builder(query2)
                .addIndex(INDEX)
                .addType(TYPE)
                .build();
        SearchResult result2 = client.execute(search2);
        assertTrue(result2.getErrorMessage(), result2.isSucceeded());

        CompositeAggregation composite2 = result2.getAggregations().getCompositeAggregation("composite1");
        assertEquals("composite1", composite2.getName());
        assertEquals(0, composite2.getBuckets().size());
        assertNull(composite2.getAfterKey());
    }
}
