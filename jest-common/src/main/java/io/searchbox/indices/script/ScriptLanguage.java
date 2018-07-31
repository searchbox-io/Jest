package io.searchbox.indices.script;

/**
 * As described in Elasticsearch Reference
 * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-scripting.html">
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-scripting.html
 * </a>
 */
public enum ScriptLanguage {
    GROOVY("groovy"),
    EXPRESSION("expression"),
    MUSTACHE("mustache"),
    JAVASCRIPT("javascript"),
    PAINLESS("painless"),
    PYTHON("python");

    public final String pathParameterName;

    ScriptLanguage(String pathParameterName) {
        this.pathParameterName = pathParameterName;
    }
}
