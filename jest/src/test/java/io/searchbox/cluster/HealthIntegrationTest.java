package io.searchbox.cluster;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author Neil Gentleman
 */


@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class HealthIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void health() throws Exception {
        JestResult result = client.execute(new Health.Builder().build());
        assertNotNull(result);
        assertThat(
                result.getJsonObject().get("status").getAsString(),
                anyOf(equalTo("green"), equalTo("yellow"), equalTo("red"))
        );
        assertTrue(result.isSucceeded());
    }

}
