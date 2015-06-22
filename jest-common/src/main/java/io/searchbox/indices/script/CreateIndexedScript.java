package io.searchbox.indices.script;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;
import static java.nio.charset.Charset.defaultCharset;

public class CreateIndexedScript extends GenericResultAbstractAction {

  private final String scriptName;
  private final ScriptLanguage scriptLanguage;

  public CreateIndexedScript(Builder builder) {
    super(builder);
    this.payload = builder.payload;
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
      return loadSource(srcFile, defaultCharset());
    }

    public Builder loadSource(File srcFile, Charset encoding) throws IOException {
      return loadSource(new FileInputStream(srcFile), encoding);
    }

    public Builder loadSource(InputStream srcStream) throws IOException {
      return loadSource(srcStream, defaultCharset());
    }

    public Builder loadSource(InputStream srcStream, Charset encoding) throws IOException {
      String src = readFully(srcStream, encoding);
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

    private String readFully(InputStream srcStream, Charset encoding) throws IOException {
      byte[] buf = new byte[8192];
      StringBuilder sb = new StringBuilder();
      for (int read; (read = srcStream.read(buf)) > 0; ) {
        sb.append(new String(buf, 0, read, encoding));
      }
      return sb.toString();
    }
  }
}
