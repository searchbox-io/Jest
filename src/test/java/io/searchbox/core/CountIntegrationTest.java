package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
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

    private static final double DELTA = 1e-15;


    @Test
    @ElasticsearchIndex(indexName = "cvbank")
    public void searchWithValidQuery() {
        String query = "{\n" +
                "    \"term\" : { \"user\" : \"nouser\" }\n" +
                "}";

        try {
            JestResult result = client.execute(new Count(query));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            assertEquals(0.0, result.getSourceAsObject(Double.class).doubleValue(),DELTA);
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


    @Test
    @ElasticsearchIndex(indexName = "cvbank")
    public void searchWithValidTermQuery() {
        String query = "{\n" +
                "\"term\" : { \"user\" : \"kimchy\" }\n" +
                "}";

        try {
            Index index = new Index.Builder("{ \"user\":\"kimchy\" }").index("cvbank").type("candidate").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            Count count = new Count(query);
            count.addIndex("cvbank");
            count.addType("candidate");

            JestResult result = client.execute(count);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            assertEquals(1.0, result.getSourceAsObject(Double.class).doubleValue(),DELTA);
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

}
