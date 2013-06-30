package io.searchbox.core.search.facet;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchMapping;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchMappingField;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author ferhat
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class TermsStatsFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "terms_stats_facet",
            mappings = {
                    @ElasticsearchMapping(typeName = "document",
                            properties = {
                                    @ElasticsearchMappingField(name = "tag", store = ElasticsearchMappingField.Store.Yes,
                                            type = ElasticsearchMappingField.Types.String),
                                    @ElasticsearchMappingField(name = "price", store = ElasticsearchMappingField.Store.Yes,
                                            type = ElasticsearchMappingField.Types.Integer)
                            })

            })
    public void testQuery() {

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

        try {
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
            assertEquals(24.0, termsStatsList.get(0).getTotal());
            assertEquals(12.0, termsStatsList.get(0).getMin());
            assertEquals(12.0, termsStatsList.get(0).getMax());
            assertEquals(12.0, termsStatsList.get(0).getMean());

            assertEquals("test", termsStatsList.get(1).getTerm());
            assertEquals(2L, termsStatsList.get(1).getCount().longValue());
            assertEquals(2L, termsStatsList.get(1).getTotalCount().longValue());
            assertEquals(70.0, termsStatsList.get(1).getTotal());
            assertEquals(30.0, termsStatsList.get(1).getMin());
            assertEquals(40.0, termsStatsList.get(1).getMax());
            assertEquals(35.0, termsStatsList.get(1).getMean());


        } catch (Exception e) {
            fail("Failed during terms facet tests " + e.getMessage());
        }

    }
}
