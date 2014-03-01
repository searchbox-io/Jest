package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author ferhat
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 2)
public class StatusIntegrationTest extends AbstractIntegrationTest {

    @Before
    public void setup() {
        createIndex("twitter", "facebook", "linkedin");
        ensureGreen("twitter", "facebook", "linkedin");
    }

    @Test
    public void testStatus() throws IOException {
        Status status = new Status.Builder().addIndex("twitter").build();

        JestResult result = client.execute(status);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        Map indices = (Map) result.getJsonMap().get("indices");
        assertEquals(1, indices.size());
        assertNotNull(indices.get("twitter"));
    }

    @Test
    public void testStatusWithAllIndices() throws IOException {
        Status status = new Status.Builder().build();
        JestResult result = client.execute(status);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        Map indices = (Map) result.getJsonMap().get("indices");
        assertEquals("All stats should include 3 indices", 3, indices.size());
        assertNotNull(indices.get("twitter"));
        assertNotNull(indices.get("facebook"));
        assertNotNull(indices.get("linkedin"));
    }

    @Test
    public void testStatusWithMultipleIndices() throws IOException {
        Status status = new Status.Builder().addIndex(Arrays.asList("facebook,twitter")).build();
        JestResult result = client.execute(status);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        Map indices = (Map) result.getJsonMap().get("indices");
        assertTrue(indices.size() == 2);
        assertNotNull(indices.get("twitter"));
        assertNotNull(indices.get("facebook"));
    }

}
