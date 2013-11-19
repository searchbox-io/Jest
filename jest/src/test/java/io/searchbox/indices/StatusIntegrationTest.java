package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.Index;
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
        DeleteIndex deleteIndex = new DeleteIndex.Builder("_all").build();
        client.execute(deleteIndex);
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void testStatus() {
        Status status = new Status.Builder().addIndex("twitter").build();
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
            Index index = new Index.Builder("{\"user\":\"test\"}").index("twitter").type("tweet").refresh(true).build();
            client.execute(index);

            index = new Index.Builder("{\"user\":\"test\"}").index("facebook").type("users").refresh(true).build();
            client.execute(index);

            Status status = new Status.Builder().build();
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
            Index index = new Index.Builder("{\"user\":\"test\"}").index("twitter").type("tweet").refresh(true).build();
            client.execute(index);

            index = new Index.Builder("{\"user\":\"test\"}").index("facebook").type("users").refresh(true).build();
            client.execute(index);

            index = new Index.Builder("{\"user\":\"test\"}").index("linkedin").type("users").refresh(true).build();
            client.execute(index);

            Status status = new Status.Builder().addIndex(Arrays.asList("facebook,twitter")).build();
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
