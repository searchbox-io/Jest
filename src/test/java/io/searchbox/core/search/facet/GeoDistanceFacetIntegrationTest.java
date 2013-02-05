package io.searchbox.core.search.facet;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Parameters;
import io.searchbox.client.JestResult;
import io.searchbox.core.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.PutMapping;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * @author ferhat
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class GeoDistanceFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "geo_distance_facet")
    public void testQuery() {

        try {
            PutMapping putMapping = new PutMapping("geo_distance_facet", "document",
                    "{ \"document\" : { \"properties\" : { \"pin.location\" : { \"type\" : \"geo_point\" } } } }");
            client.execute(putMapping);

            String query = "{\n" +
                    "    \"query\" : {\n" +
                    "        \"match_all\" : {}\n" +
                    "    },\n" +
                    "    \"facets\" : {\n" +
                    "        \"geo1\" : {\n" +
                    "            \"geo_distance\" : {\n" +
                    "                \"pin.location\" : {\n" +
                    "                    \"lat\" : 40,\n" +
                    "                    \"lon\" : -70\n" +
                    "                },\n" +
                    "                \"ranges\" : [\n" +
                    "                    { \"to\" : 1000 },\n" +
                    "                    { \"from\" : 1000, \"to\" : 2000 },\n" +
                    "                    { \"from\" : 2000, \"to\" : 10000 },\n" +
                    "                    { \"from\" : 10000 }\n" +
                    "                ]\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";


            for (int i = 0; i < 2; i++) {
                Index index = new Index.Builder("{ \"pin\" : { \"location\" : { \"lat\" : 40.12, \"lon\" : -71.34 } } }").index("geo_distance_facet").type("document").build();
                index.addParameter(Parameters.REFRESH, true);
                client.execute(index);
            }

            Index index = new Index.Builder("{ \"pin\" : { \"location\" : { \"lat\" : 30.12, \"lon\" : -61.34 } } }").index("geo_distance_facet").type("document").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            index = new Index.Builder("{ \"pin\" : { \"location\" : { \"lat\" : 10.12, \"lon\" : -31.34 } } }").index("geo_distance_facet").type("document").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            Search search = new Search(query);
            search.addIndex("geo_distance_facet");
            search.addType("document");
            JestResult result = client.execute(search);
            assertNotNull(result);

        } catch (Exception e) {
            fail("Failed during facet tests " + e.getMessage());
        }

    }
}
