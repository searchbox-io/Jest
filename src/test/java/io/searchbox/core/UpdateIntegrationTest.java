package io.searchbox.core;



import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.ElasticSearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class UpdateIntegrationTest extends AbstractIntegrationTest{

    @Test
    public void updateWithValidParameters() {
        String script = "{\n" +
                "    \"script\" : \"ctx._source.tags += tag\",\n" +
                "    \"params\" : {\n" +
                "        \"tag\" : \"blue\"\n" +
                "    }\n" +
                "}";
        try {
            ElasticSearchResult result = client.execute(new Update.Builder(script).index("twitter").type("tweet").id("1").build());
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the update with valid parameters. Exception:" + e.getMessage());
        }
    }
}
