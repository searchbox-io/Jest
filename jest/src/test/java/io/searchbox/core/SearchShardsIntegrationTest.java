package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numDataNodes = 1)
public class SearchShardsIntegrationTest extends AbstractIntegrationTest {
    static final String INDEX = "twitter";
    static final String TYPE = "tweet";

    @Test
    public void testBasicFlow() throws IOException {
        SearchShards searchShards = new SearchShards.Builder()
                .addIndex(INDEX)
                .addType(TYPE)
                .build();
        JestResult result = client.execute(searchShards);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        Map source = result.getSourceAsObject(Map.class);
        assertTrue(source.containsKey("nodes"));
        assertTrue(source.containsKey("shards"));
    }

}
