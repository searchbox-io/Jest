package io.searchbox.cluster;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

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

}
