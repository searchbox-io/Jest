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
public class FilterAggregationIntegrationTest extends AbstractIntegrationTest {

    private final String INDEX = "filter_aggregation";
    private final String TYPE = "document";

    @Test
    public void testGetFilterAggregation()
            throws IOException {

        createIndex(INDEX);
        AcknowledgedResponse AcknowledgedResponse = client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                        .type(TYPE)
                .source("{\"document\":{\"properties\":{\"num\":{\"store\":true,\"type\":\"integer\"}}}}", XContentType.JSON)
        ).actionGet();

        assertTrue(AcknowledgedResponse.isAcknowledged());

        index(INDEX, TYPE, null, "{\"num\":2}");
        index(INDEX, TYPE, null, "{\"num\":3}");
        index(INDEX, TYPE, null, "{\"num\":4}");
        refresh();
        ensureSearchable(INDEX);

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"filter1\" : {\n" +
                "            \"filter\" : { \"range\" : { \"num\" : { \"gt\" :  2 } } }" +
                "        }\n" +
                "    }\n" +
                "}";
        Search search = new Search.Builder(query)
                .addIndex(INDEX)
                .addType(TYPE)
                .build();
        SearchResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        FilterAggregation filter = result.getAggregations().getFilterAggregation("filter1");
        assertEquals("filter1", filter.getName());
        assertTrue(2L == filter.getCount());

        Aggregation aggregation = result.getAggregations().getAggregation("filter1", FilterAggregation.class);
        assertTrue(aggregation instanceof FilterAggregation);
        FilterAggregation filterByType = (FilterAggregation) aggregation;
        assertEquals(filter, filterByType);

        Map<String, Class> nameToTypeMap = new HashMap<String, Class>();
        nameToTypeMap.put("filter1", FilterAggregation.class);
        List<Aggregation> aggregations = result.getAggregations().getAggregations(nameToTypeMap);
        assertEquals(1, aggregations.size());
        assertTrue(aggregations.get(0) instanceof FilterAggregation);
        FilterAggregation filterWithMap = (FilterAggregation) aggregations.get(0);
        assertEquals(filter, filterWithMap);
    }
}
