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
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 1)
public class UpdateSettingsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void nullSourceShouldFailOnServer() throws IOException {
        UpdateSettings updateSettings = new UpdateSettings.Builder(null).build();
        JestResult result = client.execute(updateSettings);
        assertFalse(result.isSucceeded());
    }

    @Test
    public void transientSettingShouldBeUpdated() throws IOException {
        String source = "{\n" +
                "    \"transient\" : {\n" +
                "        \"discovery.zen.publish_timeout\" : 10\n" +
                "    }\n" +
                "}";

        UpdateSettings updateSettings = new UpdateSettings.Builder(source).build();
        JestResult result = client.execute(updateSettings);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        Map updatedSettings = result.getSourceAsObject(Map.class);
        assertTrue((Boolean) updatedSettings.get("acknowledged"));
        Map transientSettings = (Map) updatedSettings.get("transient");
        assertEquals(1, transientSettings.size());
        Map persistentSettings = (Map) updatedSettings.get("persistent");
        assertTrue(persistentSettings.isEmpty());
    }

}
