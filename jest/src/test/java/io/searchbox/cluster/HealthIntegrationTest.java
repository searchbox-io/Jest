package io.searchbox.cluster;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertAcked;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author Neil Gentleman
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class HealthIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void health() throws Exception {
        JestResult result = client.execute(new Health.Builder().build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertThat(
                result.getJsonObject().get("status").getAsString(),
                anyOf(equalTo("green"), equalTo("yellow"), equalTo("red"))
        );
    }

    @Test
    public void healthWithIndex() throws Exception {
        assertAcked(prepareCreate("test1").get());
        final Health request = new Health.Builder()
                .addIndex("test1")
                .build();
        JestResult result = client.execute(request);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertThat(
                result.getJsonObject().get("status").getAsString(),
                anyOf(equalTo("green"), equalTo("yellow"), equalTo("red"))
        );
    }

    @Test
    public void healthWaitForStatus() throws Exception {
        final Health request = new Health.Builder()
                .waitForStatus(Health.Status.GREEN)
                .build();
        JestResult result = client.execute(request);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals("green", result.getJsonObject().get("status").getAsString());
    }

    @Test
    public void healthWithTimeout() throws Exception {
        final Health request = new Health.Builder()
                .addIndex("test1")
                .timeout(1)
                .build();
        JestResult result = client.execute(request);
        assertFalse(result.getErrorMessage(), result.isSucceeded());
        assertEquals(408, result.getResponseCode());
    }

    @Test
    public void healthOnlyLocal() throws Exception {
        final Health request = new Health.Builder()
                .local()
                .build();
        JestResult result = client.execute(request);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertThat(
                result.getJsonObject().get("status").getAsString(),
                anyOf(equalTo("green"), equalTo("yellow"), equalTo("red"))
        );
    }

    @Test
    public void healthWaitForNoRelocatingShards() throws Exception {
        final Health request = new Health.Builder()
                .waitForNoRelocatingShards()
                .build();
        JestResult result = client.execute(request);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertThat(
                result.getJsonObject().get("status").getAsString(),
                anyOf(equalTo("green"), equalTo("yellow"), equalTo("red"))
        );
    }

    @Test
    public void healthLevelShards() throws Exception {
        final Health request = new Health.Builder()
                .level(Health.Level.SHARDS)
                .build();
        JestResult result = client.execute(request);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertThat(
                result.getJsonObject().get("status").getAsString(),
                anyOf(equalTo("green"), equalTo("yellow"), equalTo("red"))
        );
    }
}
