package io.searchbox.indices.script;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;

import java.io.UnsupportedEncodingException;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;
import static java.net.URLEncoder.encode;

public class DeleteIndexedScript extends AbstractIndexedScript {

    protected DeleteIndexedScript(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    public static class Builder extends AbstractIndexedScript.Builder<DeleteIndexedScript, Builder> {

        public Builder(String scriptName) {
            super(scriptName);
        }

        @Override
        public DeleteIndexedScript build() {
            return new DeleteIndexedScript(this);
        }

    }
}
