package io.searchbox.indices.script;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import static io.searchbox.indices.script.ScriptLanguage.GROOVY;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class CreateIndexedScriptIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void createAnIndexedScript() throws IOException {
        String name = "script-test";
        String script = "def aVariable = 1\n" +
                "return aVariable";

        CreateStoredScript createIndexedScript = new CreateStoredScript.Builder(name)
                .setLanguage(GROOVY)
                .setSource(script)
                .build();
        JestResult result = client.execute(createIndexedScript);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        GetStoredScriptResponse getIndexedScriptResponse =
                client().admin().cluster().prepareGetStoredScript().setId(name).get();
        assertNotNull(getIndexedScriptResponse.getStoredScript());
        assertEquals(script, getIndexedScriptResponse.getStoredScript());
    }
}

