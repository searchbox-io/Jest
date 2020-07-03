package io.searchbox.indices.script;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class DeleteStoredScriptIntegrationTest extends AbstractIntegrationTest {

    private static final String A_SCRIPT_NAME = "script-test";

    String script = "{\n" +
            "          \"script\": {\n" +
            "              \"lang\" : \"painless\",\n" +
            "              \"source\" : \"int aVariable = 1; return aVariable\"\n" +
            "            }\n" +
            "        }";
    @Test
    public void delete_a_stored_script_for_Painless() throws Exception {
        AcknowledgedResponse response = client().admin().cluster().preparePutStoredScript()
                .setId(A_SCRIPT_NAME)
                .setContent(new BytesArray(script), XContentType.JSON).get();
        assertTrue("could not create stored script on server", response.isAcknowledged());

        DeleteStoredScript deleteStoredScript = new DeleteStoredScript.Builder(A_SCRIPT_NAME)
                .setLanguage(ScriptLanguage.PAINLESS)
                .build();
        JestResult result = client.execute(deleteStoredScript);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        GetStoredScriptResponse getStoredScriptResponse =
                client().admin().cluster().prepareGetStoredScript()
                        .setId(A_SCRIPT_NAME).get();
        assertNull("Script should have been deleted", getStoredScriptResponse.getSource());
    }
}

