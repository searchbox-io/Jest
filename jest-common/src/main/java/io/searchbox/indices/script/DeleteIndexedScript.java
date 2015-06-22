package io.searchbox.indices.script;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;

public class DeleteIndexedScript extends GenericResultAbstractAction {

  private final String scriptName;
  private final ScriptLanguage scriptLanguage;

  public DeleteIndexedScript(Builder builder) {
    super(builder);
    this.scriptName = builder.scriptName;
    this.scriptLanguage = builder.scriptLanguage;
    setURI(buildURI());
  }

  protected String buildURI() {
    try {
      return super.buildURI() + "/_scripts/" + scriptLanguage.pathParameterName + "/" + URLEncoder.encode(scriptName, CHARSET);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
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
