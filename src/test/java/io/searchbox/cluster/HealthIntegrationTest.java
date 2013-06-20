package io.searchbox.cluster;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.*;

/**
 * @author Neil Gentleman
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class HealthIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void health() throws Exception {
        JestResult result = client.execute(new Health());
        assertNotNull(result);
        assertEquals("yellow", result.getJsonObject().get("status").getAsString());
        assertTrue(result.isSucceeded());
    }

}
