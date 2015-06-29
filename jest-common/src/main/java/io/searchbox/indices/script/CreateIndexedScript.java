package io.searchbox.indices.script;

import com.google.common.io.CharStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;

import java.io.*;
import java.nio.charset.Charset;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;
import static java.net.URLEncoder.encode;

public class CreateIndexedScript extends GenericResultAbstractAction {

    private final String scriptName;
    private final ScriptLanguage scriptLanguage;

    protected CreateIndexedScript(Builder builder) {
        super(builder);
        this.payload = builder.payload;
        this.scriptName = builder.scriptName;
        this.scriptLanguage = builder.scriptLanguage;
        setURI(buildURI());
    }

    protected String buildURI() {
        String finalUri = super.buildURI() + "/_scripts/" + scriptLanguage.pathParameterName + "/";
        try {
            finalUri += encode(scriptName, CHARSET);
        } catch (UnsupportedEncodingException e) {
            // unless CHARSET is overridden with a wrong value in a subclass,
            // this exception won't be thrown.
            log.error("Error occurred while adding parameters to uri.", e);
        }
        return finalUri;
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public String getScriptName() {
        return scriptName;
    }

    public ScriptLanguage getScriptLanguage() {
        return scriptLanguage;
    }

    public static class Builder extends AbstractAction.Builder<CreateIndexedScript, Builder> {

        private String scriptName;
        private JsonElement payload;
        private ScriptLanguage scriptLanguage = GROOVY;

        public Builder(String scriptName) {
            this.scriptName = scriptName;
        }

        @Override
        public CreateIndexedScript build() {
            return new CreateIndexedScript(this);
        }

        public Builder setSource(String source) {
            createPayload(source);
            return this;
        }

        public Builder loadSource(File srcFile) throws IOException {
            return loadSource(srcFile, Charset.forName(AbstractAction.CHARSET));
        }

        public Builder loadSource(File srcFile, Charset encoding) throws IOException {
            return loadSource(new FileInputStream(srcFile), encoding);
        }

        public Builder loadSource(InputStream srcStream) throws IOException {
            return loadSource(srcStream, Charset.forName(AbstractAction.CHARSET));
        }

        public Builder loadSource(InputStream srcStream, Charset encoding) throws IOException {
            String src = CharStreams.toString(new InputStreamReader(srcStream, encoding));
            createPayload(src);
            return this;
        }

        public Builder setLanguage(ScriptLanguage language) {
            this.scriptLanguage = language;
            return this;
        }

        private void createPayload(String source) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("script", source);
            payload = jsonObject;
        }

    }
}
