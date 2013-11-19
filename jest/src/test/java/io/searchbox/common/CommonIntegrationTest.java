package io.searchbox.common;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.searchbox.AbstractAction;
import io.searchbox.client.JestResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author ferhat
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
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

        assertEquals("true", jsonObject.get("ok").getAsString());
        assertEquals("200", jsonObject.get("status").getAsString());
        JsonObject versionObj = jsonObject.get("version").getAsJsonObject();
        assertEquals("0.90.5", versionObj.get("number").getAsString());
    }
}
