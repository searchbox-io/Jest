package io.searchbox.core.search.facet;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author ferhat
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class TermsFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "terms_facet")
    public void testQuery() throws IOException {

       /* String query = "{\n" +
                "            \"query\" : {\n" +
                "            \"match_all\" : {  }\n" +
                "        },\n" +
                "            \"facets\" : {\n" +
                "            \"tag\" : {\n" +
                "                \"terms\" : {\n" +
                "                    \"field\" : \"tag\",\n" +
                "                            \"size\" : 10\n" +
                "                }\n" +
                "            },\n" +
                "            \"user\" : {\n" +
                "                \"terms\" : {\n" +
                "                    \"field\" : \"user\",\n" +
                "                            \"size\" : 10\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        }";*/


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.facet(FacetBuilders.termsFacet("tag").field("tag").size(10)).facet(FacetBuilders.termsFacet("user").field("user").size(10));
        String query = searchSourceBuilder.toString();

        try {
            for (int i = 0; i < 2; i++) {
                Index index = new Index.Builder("{\"tag\":\"value\", \"user\":\"root\"}")
                        .index("terms_facet")
                        .type("document")
                        .refresh(true)
                        .build();
                client.execute(index);
            }

            Index index = new Index.Builder("{\"tag\":\"test\", \"user\":\"none\"}")
                    .index("terms_facet")
                    .type("document")
                    .refresh(true)
                    .build();
            client.execute(index);

            Search search = (Search) new Search.Builder(query)
                    .addIndex("terms_facet")
                    .addType("document")
                    .build();
            JestResult result = client.execute(search);
            List<TermsFacet> termsFacets = result.getFacets(TermsFacet.class);

            assertEquals(2, termsFacets.size());

            TermsFacet termsFacetFirst = termsFacets.get(0);
            assertEquals("tag", termsFacetFirst.getName());
            assertTrue(3L == termsFacetFirst.getTotal());
            assertTrue(0L == termsFacetFirst.getMissing());
            assertTrue(0L == termsFacetFirst.getOther());
            assertTrue(termsFacetFirst.terms().size() == 2);
            assertEquals("value", termsFacetFirst.terms().get(0).getName());
            assertTrue(2 == termsFacetFirst.terms().get(0).getCount());
            assertEquals("test", termsFacetFirst.terms().get(1).getName());
            assertTrue(1 == termsFacetFirst.terms().get(1).getCount());

            TermsFacet termsFacetSecond = termsFacets.get(1);
            assertEquals("user", termsFacetSecond.getName());
            assertTrue(3L == termsFacetSecond.getTotal());
            assertTrue(0L == termsFacetSecond.getMissing());
            assertTrue(0L == termsFacetSecond.getOther());
            assertTrue(termsFacetSecond.terms().size() == 2);
            assertEquals("root", termsFacetSecond.terms().get(0).getName());
            assertTrue(2 == termsFacetSecond.terms().get(0).getCount());
            assertEquals("none", termsFacetSecond.terms().get(1).getName());
            assertTrue(1 == termsFacetSecond.terms().get(1).getCount());

        } catch (Exception e) {
            fail("Failed during terms facet tests " + e.getMessage());
        }
    }
}
