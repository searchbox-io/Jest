package io.searchbox.core;


import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.params.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class UpdateIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void updateWithValidParameters() throws Exception {
        String script = "{\n" +
                "    \"script\" : \"ctx._source.tags += tag\",\n" +
                "    \"params\" : {\n" +
                "        \"tag\" : \"blue\"\n" +
                "    }\n" +
                "}";
        Index index = new Index.Builder("{\"user\":\"kimchy\", \"tags\":\"That is test\"}")
                .index("twitter")
                .type("tweet")
                .id("1")
                .setParameter(Parameters.REFRESH, true)
                .build();
        client.execute(index);

        JestResult result = client.execute(new Update.Builder(script).index("twitter").type("tweet").id("1").build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        JestResult getResult = client.execute(new Get.Builder("twitter", "1").type("tweet").build());
        assertEquals("That is testblue", ((Map) getResult.getValue("_source")).get("tags"));
    }
}
