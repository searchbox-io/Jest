package io.searchbox.indices.script;

import org.junit.Before;
import org.junit.Test;

import static io.searchbox.indices.script.ScriptLanguage.GROOVY;
import static io.searchbox.indices.script.ScriptLanguage.JAVASCRIPT;
import static org.fest.assertions.Assertions.assertThat;

public class DeleteIndexedScriptTest {

  private static final String A_NAME = "a_name";
  private DeleteIndexedScript script;

  @Before
  public void setUp() throws Exception {
    DeleteIndexedScript.Builder builder = new DeleteIndexedScript.Builder(A_NAME).setLanguage(JAVASCRIPT);
    script = builder.build();
  }

  @Test
  public void defaultScriptingLanguageIsGroovy() throws Exception {
    DeleteIndexedScript script = new DeleteIndexedScript.Builder(A_NAME).build();

    assertThat(script.getScriptLanguage()).isEqualTo(GROOVY);
    assertThat(script.buildURI()).contains(GROOVY.pathParameterName);
  }

  @Test
  public void scriptingLanguageIsSetIntoPath() throws Exception {

    assertThat(script.buildURI()).contains("/_scripts/" + JAVASCRIPT.pathParameterName + "/");
  }

  @Test
  public void nameOfTheScriptIsSetIntoPath() throws Exception {

    assertThat(script.buildURI()).contains("/_scripts/" + JAVASCRIPT.pathParameterName + "/" + A_NAME);
  }

}