package io.searchbox.core.search.facet;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author ferhat
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class TermsFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testQuery() throws IOException {
        createIndex("terms_facet");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.facet(FacetBuilders.termsFacet("tag").field("tag").size(10)).facet(FacetBuilders.termsFacet("user").field("user").size(10));
        String query = searchSourceBuilder.toString();

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
    }
}
