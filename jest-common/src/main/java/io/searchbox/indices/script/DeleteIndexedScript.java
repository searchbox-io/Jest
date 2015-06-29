package io.searchbox.indices.script;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;

import java.io.UnsupportedEncodingException;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;
import static java.net.URLEncoder.encode;

public class DeleteIndexedScript extends GenericResultAbstractAction {

  private final String scriptName;
  private final ScriptLanguage scriptLanguage;

  protected DeleteIndexedScript(Builder builder) {
    super(builder);
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
    return "DELETE";
  }

  public String getScriptName() {
    return scriptName;
  }

  public ScriptLanguage getScriptLanguage() {
    return scriptLanguage;
  }

  public static class Builder extends AbstractAction.Builder<DeleteIndexedScript, Builder> {

    private String scriptName;
    private ScriptLanguage scriptLanguage = GROOVY;

    public Builder(String scriptName) {
      this.scriptName = scriptName;
    }

    public Builder setLanguage(ScriptLanguage scriptLanguage) {
      this.scriptLanguage = scriptLanguage;
      return this;
    }

    @Override
    public DeleteIndexedScript build() {
      return new DeleteIndexedScript(this);
    }

  }
}
