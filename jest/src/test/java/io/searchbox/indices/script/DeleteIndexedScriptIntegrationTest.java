package io.searchbox.indices.script;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptRequest;
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptResponse;
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptRequest;
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class DeleteIndexedScriptIntegrationTest extends AbstractIntegrationTest {

    private static final String A_SCRIPT_NAME = "script-test";

    @Test
    public void delete_an_indexed_script_for_Groovy() throws IOException {
        PutIndexedScriptResponse response = client().putIndexedScript(
                new PutIndexedScriptRequest("groovy", A_SCRIPT_NAME)
                        .source("{\"script\":\"def aVariable = 1\\nreturn aVariable\"}")
        ).actionGet();
        assertTrue("could not create indexed script on server", response.isCreated());

        DeleteIndexedScript deleteIndexedScript = new DeleteIndexedScript.Builder(A_SCRIPT_NAME)
                .setLanguage(ScriptLanguage.GROOVY)
                .build();
        JestResult result = client.execute(deleteIndexedScript);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        GetIndexedScriptResponse getIndexedScriptResponse =
                client().getIndexedScript(new GetIndexedScriptRequest("groovy", A_SCRIPT_NAME)).actionGet();
        assertFalse("Script should have been deleted", getIndexedScriptResponse.isExists());
    }

}

