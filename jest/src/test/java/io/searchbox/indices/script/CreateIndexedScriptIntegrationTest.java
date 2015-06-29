package io.searchbox.indices.script;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;

@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST, numDataNodes = 1)
public class CreateIndexedScriptIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void create_an_indexed_script_for_Groovy() throws IOException {
        CreateIndexedScript createIndexedScript = new CreateIndexedScript.Builder("script-test")
                .setLanguage(GROOVY)
                .setSource("def aVariable = 1\n" +
                        "return aVariable")
                .build();
        JestResult result = client.execute(createIndexedScript);
        assertTrue(result.isSucceeded());
    }

}

