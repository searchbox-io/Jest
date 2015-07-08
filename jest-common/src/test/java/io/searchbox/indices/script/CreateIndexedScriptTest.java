package io.searchbox.indices.script;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;
import static io.searchbox.indices.script.ScriptLanguage.JAVASCRIPT;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class CreateIndexedScriptTest {

    private static final String A_NAME = "a_name";
    private CreateIndexedScript script;
    private CreateIndexedScript.Builder builder;
    private String groovysnippet;

    @Before
    public void setUp() throws Exception {
        builder = new CreateIndexedScript.Builder(A_NAME).setLanguage(JAVASCRIPT);
        script = builder.build();
        groovysnippet = "def test_a=123\n" +
                "def test_b=\"$test_a\"\n";
    }

    @Test
    public void defaultScriptingLanguageIsGroovy() throws Exception {
        CreateIndexedScript script = new CreateIndexedScript.Builder(A_NAME).build();

        assertEquals(GROOVY, script.getScriptLanguage());
        assertThat(script.buildURI(), containsString(GROOVY.pathParameterName));
    }

    @Test
    public void methodIsPost() {
        assertEquals("POST", script.getRestMethodName());
    }

    @Test
    public void scriptingLanguageIsSetIntoPath() throws Exception {
        assertThat(script.buildURI(), containsString("/_scripts/" + JAVASCRIPT.pathParameterName + "/"));
    }

    @Test
    public void nameOfTheScriptIsSetIntoPath() throws Exception {
        assertThat(script.buildURI(), containsString("/_scripts/" + JAVASCRIPT.pathParameterName + "/" + A_NAME));
    }

    @Test
    public void scriptSourceIsValidJsonString() throws Exception {
        builder.setSource(groovysnippet);

        script = builder.build();

        JsonObject jsonPayload = parseAsGson(script.getData(new Gson()));
        assertNotNull(jsonPayload.get("script"));
        assertEquals(groovysnippet, jsonPayload.get("script").getAsString());
    }

    @Test
    public void fileSourceIsValidJsonString() throws Exception {
        builder.loadSource(createTempGroovySnippetFile());

        script = builder.build();

        JsonObject jsonPayload = parseAsGson(script.getData(new Gson()));
        assertNotNull(jsonPayload.get("script"));
        assertEquals(groovysnippet, jsonPayload.get("script").getAsString());
    }

    private File createTempGroovySnippetFile() throws IOException {
        File file = File.createTempFile("test", ".groovy");
        file.deleteOnExit();
        FileWriter writer = new FileWriter(file);
        writer.write(groovysnippet);
        writer.close();
        return file;
    }

    private JsonObject parseAsGson(String data) {
        return new JsonParser().parse(data).getAsJsonObject();
    }
}