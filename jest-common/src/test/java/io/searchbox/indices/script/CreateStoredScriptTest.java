package io.searchbox.indices.script;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static io.searchbox.indices.script.ScriptLanguage.JAVASCRIPT;
import static io.searchbox.indices.script.ScriptLanguage.PAINLESS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class CreateStoredScriptTest {

    private static final String A_NAME = "a_name";
    private CreateStoredScript script;
    private CreateStoredScript.Builder builder;
    private String groovysnippet;

    @Before
    public void setUp() {
        builder = new CreateStoredScript.Builder(A_NAME).setLanguage(JAVASCRIPT);
        script = builder.build();
        groovysnippet = "def test_a=123\n" +
                "def test_b=\"$test_a\"\n";
    }

    @Test
    public void defaultScriptingLanguageIsPainless() {
        CreateStoredScript script = new CreateStoredScript.Builder(A_NAME).build();
        assertEquals(PAINLESS, script.getScriptLanguage());
    }

    @Test
    public void scriptingLanguageIsSetCorrectly() {
        CreateStoredScript script = new CreateStoredScript.Builder(A_NAME).setLanguage(JAVASCRIPT).build();
        assertEquals(JAVASCRIPT, script.getScriptLanguage());
    }

    @Test
    public void methodIsPost() {
        assertEquals("POST", script.getRestMethodName());
    }

    @Test
    public void scriptingLanguageIsSetIntoPath() {
        assertThat(script.buildURI(ElasticsearchVersion.UNKNOWN), containsString("/_scripts/"));
    }

    @Test
    public void nameOfTheScriptIsSetIntoPath() {
        assertThat(script.buildURI(ElasticsearchVersion.UNKNOWN), containsString("/_scripts/" + A_NAME));
    }

    @Test
    public void scriptSourceIsValidJsonString() {
        builder.setSource(groovysnippet);

        script = builder.build();

        JsonObject jsonPayload = parseAsGson(script.getData(new Gson()));
        assertNotNull(jsonPayload.get("script"));
        assertEquals(groovysnippet, jsonPayload.get("script").getAsJsonObject().get("source").getAsString());
    }

    @Test
    public void fileSourceIsValidJsonString() throws Exception {
        builder.loadSource(createTempGroovySnippetFile());

        script = builder.build();

        JsonObject jsonPayload = parseAsGson(script.getData(new Gson()));
        assertNotNull(jsonPayload.get("script"));
        assertEquals(groovysnippet, jsonPayload.get("script").getAsJsonObject().get("source").getAsString());
    }

    private File createTempGroovySnippetFile() throws IOException {
        File file = File.createTempFile("test", ".groovy");
        file.deleteOnExit();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(groovysnippet);
            return file;
        }
    }

    private JsonObject parseAsGson(String data) {
        return new JsonParser().parse(data).getAsJsonObject();
    }
}