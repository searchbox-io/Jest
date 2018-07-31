package io.searchbox.indices.script;

import com.google.common.io.CharStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.action.AbstractAction;

import java.io.*;
import java.nio.charset.Charset;

public class CreateStoredScript extends AbstractStoredScript {

    protected CreateStoredScript(Builder builder) {
        super(builder);
        this.payload = builder.payload;
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

    public static class Builder extends AbstractStoredScript.Builder<CreateStoredScript, Builder> {

        private JsonElement payload;

        public Builder(String scriptName) {
            super(scriptName);
        }

        @Override
        public CreateStoredScript build() {
            return new CreateStoredScript(this);
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

        private void createPayload(String source) {
            JsonObject jsonObject = new JsonObject();

            JsonObject innerObj = new JsonObject();
            innerObj.addProperty("lang", String.valueOf(scriptLanguage).toLowerCase());
            innerObj.addProperty("source", source);

            jsonObject.add("script", innerObj);
            payload = jsonObject;
        }

    }
}
