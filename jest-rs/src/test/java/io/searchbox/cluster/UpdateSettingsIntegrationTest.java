package io.searchbox.cluster;


import java.io.IOException;
import java.util.Map;

import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Ignore;
import org.junit.Test;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 1)
public class UpdateSettingsIntegrationTest extends AbstractIntegrationTest {

    /**
     * No data rather than "null" gets sent when built with null.
     */
    @Ignore
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

        @SuppressWarnings("unchecked")
        Map<String, Object> updatedSettings = result.getSourceAsObject(Map.class);
        assertTrue((Boolean) updatedSettings.get("acknowledged"));
        @SuppressWarnings("unchecked")
        Map<String, Object> transientSettings = (Map<String, Object>) updatedSettings.get("transient");
        assertEquals(1, transientSettings.size());
        @SuppressWarnings("unchecked")
        Map<String, Object> persistentSettings = (Map<String, Object>) updatedSettings.get("persistent");
        assertTrue(persistentSettings.isEmpty());
    }

}
