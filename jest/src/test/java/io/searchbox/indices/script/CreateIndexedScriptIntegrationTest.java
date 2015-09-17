package io.searchbox.indices.script;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptRequest;
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class CreateIndexedScriptIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void create_an_indexed_script_for_Groovy() throws IOException {
        String name = "script-test";
        String script = "def aVariable = 1\n" +
                "return aVariable";

        CreateIndexedScript createIndexedScript = new CreateIndexedScript.Builder(name)
                .setLanguage(GROOVY)
                .setSource(script)
                .build();
        JestResult result = client.execute(createIndexedScript);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        GetIndexedScriptResponse getIndexedScriptResponse =
                client().getIndexedScript(new GetIndexedScriptRequest("groovy", name)).actionGet();
        assertTrue(getIndexedScriptResponse.isExists());
        assertEquals(script, getIndexedScriptResponse.getScript());
    }

}

