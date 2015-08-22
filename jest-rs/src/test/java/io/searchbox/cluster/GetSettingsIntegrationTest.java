package io.searchbox.cluster;


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
public class GetSettingsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void nullSourceShouldFailOnServer() throws IOException {
        GetSettings getSettings = new GetSettings.Builder().build();
        JestResult result = client.execute(getSettings);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        @SuppressWarnings("unchecked")
        Map<String, Object> settings  = result.getSourceAsObject(Map.class);
        assertTrue(settings.containsKey("persistent"));
        assertTrue(settings.containsKey("transient"));
    }

}
