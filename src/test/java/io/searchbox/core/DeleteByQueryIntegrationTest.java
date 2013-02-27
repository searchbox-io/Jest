package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class DeleteByQueryIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void delete() {
        String query = "{\n" +
                "    \"term\" : { \"user\" : \"kimchy\" }\n" +
                "}";

        try {
            client.execute(new Index.Builder("\"user\":\"kimchy\"").index("twitter").type("tweet").id("1").build());
            DeleteByQuery deleteByQuery = new DeleteByQuery(query);
            deleteByQuery.addIndex("twitter");
            deleteByQuery.addType("tweet");

            JestResult result = client.execute(deleteByQuery);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            assertEquals(1.0, ((Map) ((Map) ((Map) (result.getJsonMap().get("_indices"))).get("twitter")).get("_shards")).get("successful"));

        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

}
