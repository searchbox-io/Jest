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
public class MultiSearchIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void multiSearch() throws IOException {
        Search search = new Search.Builder("{\"match_all\" : {}}").build();
        MultiSearch multiSearch = new MultiSearch.Builder(search).build();
        executeTestCase(multiSearch);
    }

    @Test
    public void singleMultiSearchWitIndex() throws IOException {
        Search search = new Search.Builder("{\"match_all\" : {}}").addIndex("twitter").build();
        MultiSearch multiSearch = new MultiSearch.Builder(search).build();
        executeTestCase(multiSearch);
    }

    @Test
    public void MultiSearchWitIndex() throws IOException {
        Search search = new Search.Builder("{\"match_all\" : {}}").addIndex("twitter").build();
        Search search2 = new Search.Builder("{\"match_all\" : {}}").build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).addSearch(search2).build();
        executeTestCase(multiSearch);
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);

        assertTrue(result.isSucceeded());
    }

}
