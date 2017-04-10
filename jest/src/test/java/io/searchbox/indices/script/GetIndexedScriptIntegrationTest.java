package io.searchbox.indices.script;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptResponse;
import org.elasticsearch.common.bytes.BytesArray;
import org.junit.Test;

import java.io.IOException;

public class GetIndexedScriptIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void createIndexedScript() throws IOException {
        String name = "mylilscript";

        PutStoredScriptResponse response = client().admin().cluster().preparePutStoredScript().setId(name)
                .setScriptLang(ScriptLanguage.PAINLESS.pathParameterName)
                        .setSource(new BytesArray("{\"script\": \"return 42;\"}".getBytes())).get();
        assertTrue("could not create indexed script on server", response.isAcknowledged());

        GetStoredScript getIndexedScript = new GetStoredScript.Builder(name)
                .setLanguage(ScriptLanguage.PAINLESS)
                .build();
        JestResult result = client.execute(getIndexedScript);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

}