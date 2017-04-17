package io.searchbox.indices.script;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptResponse;
import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptResponse;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class DeleteIndexedScriptIntegrationTest extends AbstractIntegrationTest {

    private static final String A_SCRIPT_NAME = "script-test";

    @Test
    public void delete_an_indexed_script_for_Groovy() throws Exception {
        PutStoredScriptResponse response = client().admin().cluster().preparePutStoredScript()
                .setScriptLang("painless")
                .setId(A_SCRIPT_NAME)
                .setSource(new BytesArray("{\"script\":\"int aVariable = 1; return aVariable\"}")).get();
        assertTrue("could not create indexed script on server", response.isAcknowledged());

        DeleteStoredScript deleteIndexedScript = new DeleteStoredScript.Builder(A_SCRIPT_NAME)
                .setLanguage(ScriptLanguage.PAINLESS)
                .build();
        JestResult result = client.execute(deleteIndexedScript);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        GetStoredScriptResponse getIndexedScriptResponse =
                client().admin().cluster().prepareGetStoredScript()
                        .setLang("painless")
                        .setId(A_SCRIPT_NAME).get();
        assertNull("Script should have been deleted", getIndexedScriptResponse.getStoredScript());
    }
}

