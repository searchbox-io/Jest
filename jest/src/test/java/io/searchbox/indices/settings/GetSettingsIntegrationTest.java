package io.searchbox.indices.settings;

import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class GetSettingsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testBasicFlow() throws IOException {
        String index = "test";

        createIndex(index);
        ensureGreen(index);

        GetSettings getSettings = new GetSettings.Builder().build();
        JestResult result = client.execute(getSettings);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        assertTrue(result.isSucceeded());
        System.out.println("result.getJsonString() = " + result.getJsonString());
        JsonObject json = result.getJsonObject();
        assertNotNull(json.getAsJsonObject(index));
        assertNotNull(json.getAsJsonObject(index).getAsJsonObject("settings"));
    }

    @Test
    public void testForNonexistentIndex() throws IOException {
        String index = "test";

        createIndex(index);
        ensureGreen(index);

        GetSettings getSettings = new GetSettings.Builder().addIndex("nonExisting").build();
        JestResult result = client.execute(getSettings);
        assertFalse(result.isSucceeded());
    }

}