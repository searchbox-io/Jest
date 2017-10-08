package io.searchbox.indices.script;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;

import java.io.UnsupportedEncodingException;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;
import static java.net.URLEncoder.encode;

/**
 * @author cihat keser
 */
public abstract class AbstractIndexedScript extends GenericResultAbstractAction {
    protected String scriptName;
    protected ScriptLanguage scriptLanguage;

    protected AbstractIndexedScript(Builder builder) {
        super(builder);
        this.scriptName = builder.scriptName;
        this.scriptLanguage = builder.scriptLanguage;
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

    public String getScriptName() {
        return scriptName;
    }

    public ScriptLanguage getScriptLanguage() {
        return scriptLanguage;
    }

    @SuppressWarnings("unchecked")
    public static abstract class Builder<T extends AbstractIndexedScript, K> extends AbstractAction.Builder<T, K> {

        private String scriptName;
        private ScriptLanguage scriptLanguage = GROOVY;

        public Builder(String scriptName) {
            this.scriptName = scriptName;
        }

        public K setLanguage(ScriptLanguage scriptLanguage) {
            this.scriptLanguage = scriptLanguage;
            return (K) this;
        }

    }
}
