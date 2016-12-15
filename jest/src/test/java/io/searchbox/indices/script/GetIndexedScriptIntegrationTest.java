package io.searchbox.indices.script;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptRequest;
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptResponse;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.groovy.GroovyPlugin;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

public class GetIndexedScriptIntegrationTest extends AbstractIntegrationTest {

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return pluginList(GroovyPlugin.class);
    }

    @Test
    public void create_an_indexed_script_for_Groovy() throws IOException {
        String name = "mylilscript";

        PutIndexedScriptResponse response = client().putIndexedScript(
                new PutIndexedScriptRequest("groovy", name)
                        .source("{\"script\":\"def aVariable = 1\\nreturn aVariable\"}")
        ).actionGet();
        assertTrue("could not create indexed script on server", response.isCreated());

        GetIndexedScript getIndexedScript = new GetIndexedScript.Builder(name)
                .setLanguage(ScriptLanguage.GROOVY)
                .build();
        JestResult result = client.execute(getIndexedScript);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

}