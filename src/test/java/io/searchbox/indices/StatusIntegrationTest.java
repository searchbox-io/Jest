package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.core.AbstractIntegrationTest;
import io.searchbox.core.Index;
import io.searchbox.params.Parameters;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author ferhat
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class StatusIntegrationTest extends AbstractIntegrationTest {

    @After
    public void clearIndices() throws IOException {
        DeleteIndex deleteIndex = new DeleteIndex("_all");
        client.execute(deleteIndex);
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void testStatus() {
        Status status = new Status();
        status.addIndex("twitter");
        try {
            JestResult result = client.execute(status);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            Map indices = (Map) result.getJsonMap().get("indices");
            assertEquals(1, indices.size());
            assertNotNull(indices.get("twitter"));
        } catch (IOException e) {
            fail("Test failed while getting index status");
        }
    }

    @Test
    public void testStatusWithAllIndices() {
        try {
            Index index = new Index.Builder("{\"user\":\"test\"}").index("twitter").type("tweet").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            index = new Index.Builder("{\"user\":\"test\"}").index("facebook").type("users").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            Status status = new Status();
            JestResult result = client.execute(status);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            Map indices = (Map) result.getJsonMap().get("indices");
            assertTrue(indices.size() == 2);
            assertNotNull(indices.get("twitter"));
            assertNotNull(indices.get("facebook"));

        } catch (IOException e) {
            fail("Test failed while getting index status");
        }
    }

    @Test
    public void testStatusWithMultipleIndices() {
        try {
            Index index = new Index.Builder("{\"user\":\"test\"}").index("twitter").type("tweet").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            index = new Index.Builder("{\"user\":\"test\"}").index("facebook").type("users").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            index = new Index.Builder("{\"user\":\"test\"}").index("linkedin").type("users").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            Status status = new Status();
            status.addIndex(Arrays.asList("facebook,twitter"));
            JestResult result = client.execute(status);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            Map indices = (Map) result.getJsonMap().get("indices");
            assertTrue(indices.size() == 2);
            assertNotNull(indices.get("twitter"));
            assertNotNull(indices.get("facebook"));

        } catch (IOException e) {
            fail("Test failed while getting index status");
        }
    }

}
