package io.searchbox.indices.script;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;

@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 1)
public class DeleteIndexedScriptIntegrationTest extends AbstractIntegrationTest {

    private static final String A_SCRIPT_NAME = "script-test";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        createIndexedScript();
    }

    @Test
    public void delete_an_indexed_script_for_Groovy() throws IOException {
        DeleteIndexedScript deleteIndexedScript = new DeleteIndexedScript.Builder(A_SCRIPT_NAME)
                .setLanguage(ScriptLanguage.GROOVY)
                .build();
        JestResult result = client.execute(deleteIndexedScript);
        assertTrue(result.isSucceeded());
    }

    private void createIndexedScript() throws IOException {
        client.execute(new CreateIndexedScript.Builder(A_SCRIPT_NAME)
                .setLanguage(GROOVY)
                .setSource("def aVariable = 1\n" +
                        "return aVariable")
                .build());
    }

}

