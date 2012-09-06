package io.searchbox.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.ElasticSearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class CountIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void searchWithValidQuery() {
        String query = "{\n" +
                "    \"term\" : { \"user\" : \"kimchy\" }\n" +
                "}";

        try {
            ElasticSearchResult result = client.execute(new Count(query));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            assertEquals(0.0, result.getSourceAsObject(Double.class));
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


    @Test
    public void searchWithValidBoolQuery() {
        String query = "{\n" +
                "    \"term\" : { \"user\" : \"kimchy\" }\n" +
                "}";

        try {
            client.execute(new Index.Builder("\"user\":\"kimchy\"").index("cvbank").type("candidate").build());

            Count count = new Count(query);
            count.addIndex("cvbank");
            count.addType("candidate");

            ElasticSearchResult result = client.execute(count);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            assertEquals(1.0, result.getSourceAsObject(Double.class));
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

}
