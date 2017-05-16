package io.searchbox.indices.script;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptResponse;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

import java.io.IOException;

public class GetIndexedScriptIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void createIndexedScript() throws IOException {
        String name = "mylilscript";

        PutStoredScriptResponse response = client().admin().cluster().preparePutStoredScript().setId(name)
                .setLang(ScriptLanguage.PAINLESS.pathParameterName)
                .setContent(new BytesArray("{\"script\": \"return 42;\"}"), XContentType.JSON).get();
        assertTrue("could not create indexed script on server", response.isAcknowledged());

        GetStoredScript getIndexedScript = new GetStoredScript.Builder(name)
                .setLanguage(ScriptLanguage.PAINLESS)
                .build();
        JestResult result = client.execute(getIndexedScript);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

}