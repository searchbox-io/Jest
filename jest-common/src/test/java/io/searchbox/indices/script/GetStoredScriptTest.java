package io.searchbox.indices.script;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Before;
import org.junit.Test;

import static io.searchbox.indices.script.ScriptLanguage.JAVASCRIPT;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author cihat keser
 */
public class GetStoredScriptTest {

    private static final String A_NAME = "a_name";
    private GetStoredScript script;

    @Before
    public void setUp() {
        GetStoredScript.Builder builder = new GetStoredScript.Builder(A_NAME).setLanguage(JAVASCRIPT);
        script = builder.build();
    }

    @Test
    public void methodIsGet() {
        assertEquals("GET", script.getRestMethodName());
    }

    @Test
    public void scriptingLanguageIsSetIntoPath() {
        assertThat(script.buildURI(ElasticsearchVersion.UNKNOWN), containsString("/_scripts/"));
    }

    @Test
    public void nameOfTheScriptIsSetIntoPath() {
        assertThat(script.buildURI(ElasticsearchVersion.UNKNOWN), containsString("/_scripts/" + A_NAME));
    }

}