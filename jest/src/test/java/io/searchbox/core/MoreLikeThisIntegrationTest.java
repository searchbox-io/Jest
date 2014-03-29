package io.searchbox.core;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class MoreLikeThisIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void moreLikeThis() throws IOException {
        executeTestCase(new MoreLikeThis.Builder("twitter", "tweet", "1", null).build());
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        assertFalse(result.isSucceeded());
    }

}
