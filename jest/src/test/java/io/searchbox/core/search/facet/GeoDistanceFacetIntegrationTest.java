package io.searchbox.core.search.facet;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author ferhat
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class GeoDistanceFacetIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testQuery() throws IOException {
        createIndex("geo_distance_facet");
        PutMapping putMapping = new PutMapping.Builder(
                "geo_distance_facet",
                "document",
                "{ \"document\" : { \"properties\" : { \"pin.location\" : { \"type\" : \"geo_point\" } } } }"
        ).build();
        client.execute(putMapping);

        String query = "{\n" +
                "                \"query\" : {\n" +
                "                \"match_all\" : {}\n" +
                "            },\n" +
                "                \"facets\" : {\n" +
                "                \"geo1\" : {\n" +
                "                    \"geo_distance\" : {\n" +
                "                        \"pin.location\" : {\n" +
                "                            \"lat\" : 40,\n" +
                "                                    \"lon\" : -70\n" +
                "                        },\n" +
                "                        \"ranges\" : [\n" +
                "                        { \"to\" : 10 },\n" +
                "                        { \"from\" : 10, \"to\" : 20 },\n" +
                "                        { \"from\" : 20, \"to\" : 100 },\n" +
                "                        { \"from\" : 100 }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "            }";

        for (int i = 0; i < 2; i++) {
            Index index = new Index.Builder("{\"pin\" : {\"location\" : {\"lat\" : 40.12,\"lon\" : -71.34},\"tag\" : [\"food\", \"family\"],\"text\" : \"my favorite family restaurant\"}}")
                    .index("geo_distance_facet")
                    .type("document")
                    .refresh(true)
                    .build();
            client.execute(index);
        }

        Index index = new Index.Builder("{ \"pin\" : { \"location\" : { \"lat\" : 30.12, \"lon\" : -61.34 } } }")
                .index("geo_distance_facet")
                .type("document")
                .refresh(true)
                .build();
        client.execute(index);

        index = new Index.Builder("{ \"pin\" : { \"location\" : { \"lat\" : 10.12, \"lon\" : -31.34 } } }")
                .index("geo_distance_facet")
                .type("document")
                .refresh(true)
                .build();
        client.execute(index);

        Search search = new Search.Builder(query)
                .addIndex("geo_distance_facet")
                .addType("document")
                .build();
        JestResult result = client.execute(search);
        List<GeoDistanceFacet> distanceFacets = result.getFacets(GeoDistanceFacet.class);

        assertEquals(1, distanceFacets.size());
        assertEquals("geo1", distanceFacets.get(0).getName());
        assertEquals(4, distanceFacets.get(0).getRanges().size());
    }
}
