package io.searchbox.indices.script;

import org.junit.Before;
import org.junit.Test;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;
import static io.searchbox.indices.script.ScriptLanguage.JAVASCRIPT;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author cihat keser
 */
public class GetIndexedScriptTest {

    private static final String A_NAME = "a_name";
    private GetIndexedScript script;

    @Before
    public void setUp() throws Exception {
        GetIndexedScript.Builder builder = new GetIndexedScript.Builder(A_NAME).setLanguage(JAVASCRIPT);
        script = builder.build();
    }

    @Test
    public void defaultScriptingLanguageIsGroovy() throws Exception {
        GetIndexedScript script = new GetIndexedScript.Builder(A_NAME).build();

        assertEquals(GROOVY, script.getScriptLanguage());
        assertThat(script.buildURI(), containsString(GROOVY.pathParameterName));
    }

    @Test
    public void methodIsGet() {
        assertEquals("GET", script.getRestMethodName());
    }

    @Test
    public void scriptingLanguageIsSetIntoPath() throws Exception {
        assertThat(script.buildURI(), containsString("/_scripts/" + JAVASCRIPT.pathParameterName + "/"));
    }

    @Test
    public void nameOfTheScriptIsSetIntoPath() throws Exception {
        assertThat(script.buildURI(), containsString("/_scripts/" + JAVASCRIPT.pathParameterName + "/" + A_NAME));
    }

}