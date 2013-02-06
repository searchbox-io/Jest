package io.searchbox.core.search.facet;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Parameters;
import io.searchbox.client.JestResult;
import io.searchbox.core.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    public void testQuery() {

        String query = "{\n" +
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
                "        }";

        try {
            for (int i = 0; i < 2; i++) {
                Index index = new Index.Builder("{\"tag\":\"value\", \"user\":\"root\"}").index("terms_facet").type("document").build();
                index.addParameter(Parameters.REFRESH, true);
                client.execute(index);
            }

            Index index = new Index.Builder("{\"tag\":\"test\", \"user\":\"none\"}").index("terms_facet").type("document").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            Search search = new Search(query);
            search.addIndex("terms_facet");
            search.addType("document");
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
