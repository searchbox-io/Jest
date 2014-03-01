package io.searchbox.common;

import com.google.gson.JsonObject;
import io.searchbox.AbstractAction;
import io.searchbox.client.JestResult;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author ferhat
 */

@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class CommonIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testVersion() throws IOException {
        JestResult result = client.execute(new AbstractAction() {
            @Override
            public String getURI() {
                return "";
            }

            @Override
            public String getRestMethodName() {
                return "GET";
            }
        });

        assertNotNull(result);

        JsonObject jsonObject = result.getJsonObject();

        assertEquals("200", jsonObject.get("status").getAsString());
        JsonObject versionObj = jsonObject.get("version").getAsJsonObject();
        assertEquals("1.0.0", versionObj.get("number").getAsString());
    }
}
