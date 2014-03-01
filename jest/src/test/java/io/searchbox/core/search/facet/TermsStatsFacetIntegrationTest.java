package io.searchbox.core.search.facet;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author ferhat
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class TermsStatsFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testQuery() throws IOException {
        createIndex("terms_stats_facet");
        client().admin().indices().putMapping(new PutMappingRequest("terms_stats_facet")
                .type("document")
                .source("{\"document\":{\"properties\":{\"tag\":{\"store\":true,\"type\":\"string\"}," +
                        "\"price\":{\"store\":true,\"type\":\"integer\"}}}}")
        ).actionGet();

        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {  }\n" +
                "    },\n" +
                "    \"facets\" : {\n" +
                "        \"tag_price_stats\" : {\n" +
                "            \"terms_stats\" : {\n" +
                "                \"key_field\" : \"tag\",\n" +
                "                \"value_field\" : \"price\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        for (int i = 0; i < 2; i++) {
            Index index = new Index.Builder("{\"tag\":\"value\", \"price\":\"12\"}")
                    .index("terms_stats_facet")
                    .type("document")
                    .refresh(true)
                    .build();
            client.execute(index);
        }

        Index index = new Index.Builder("{\"tag\":\"test\", \"price\":\"30\"}")
                .index("terms_stats_facet")
                .type("document")
                .refresh(true)
                .build();
        client.execute(index);

        index = new Index.Builder("{\"tag\":\"test\", \"price\":\"40\"}")
                .index("terms_stats_facet")
                .type("document")
                .refresh(true)
                .build();
        client.execute(index);

        Search search = (Search) new Search.Builder(query)
                .addType("document")
                .addIndex("terms_stats_facet")
                .build();
        JestResult result = client.execute(search);
        List<TermsStatsFacet> termsStatsFacets = result.getFacets(TermsStatsFacet.class);

        assertEquals(1, termsStatsFacets.size());

        TermsStatsFacet termsStatsFacet = termsStatsFacets.get(0);
        assertEquals("tag_price_stats", termsStatsFacet.getName());
        assertEquals(0L, termsStatsFacet.getMissing().longValue());

        List<TermsStatsFacet.TermsStats> termsStatsList = termsStatsFacet.getTermsStatsList();
        assertEquals(2, termsStatsList.size());

        assertEquals("value", termsStatsList.get(0).getTerm());
        assertEquals(2L, termsStatsList.get(0).getCount().longValue());
        assertEquals(2L, termsStatsList.get(0).getTotalCount().longValue());
        assertEquals(24, termsStatsList.get(0).getTotal().intValue());
        assertEquals(12, termsStatsList.get(0).getMin().intValue());
        assertEquals(12, termsStatsList.get(0).getMax().intValue());
        assertEquals(12, termsStatsList.get(0).getMean().intValue());

        assertEquals("test", termsStatsList.get(1).getTerm());
        assertEquals(2L, termsStatsList.get(1).getCount().longValue());
        assertEquals(2L, termsStatsList.get(1).getTotalCount().longValue());
        assertEquals(70, termsStatsList.get(1).getTotal().intValue());
        assertEquals(30, termsStatsList.get(1).getMin().intValue());
        assertEquals(40, termsStatsList.get(1).getMax().intValue());
        assertEquals(35, termsStatsList.get(1).getMean().intValue());
    }
}
