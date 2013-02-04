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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

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
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"facets\" : {\n" +
                "        \"tag\" : {\n" +
                "            \"terms\" : {\n" +
                "                \"field\" : \"tag\",\n" +
                "                \"size\" : 10\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        try {
            for (int i = 0; i < 2; i++) {
                Index index = new Index.Builder("{\"tag\":\"value\", \"user\":\"root\"}").index("terms_facet").type("document").build();
                index.addParameter(Parameters.REFRESH, true);
                client.execute(index);
            }
            Search search = new Search(query);
            search.addIndex("terms_facet");
            search.addType("document");
            JestResult result = client.execute(search);
            List<TermsFacet> termsFacets = result.getFacets(TermsFacet.class);
            assertNotNull(termsFacets);
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }
}
