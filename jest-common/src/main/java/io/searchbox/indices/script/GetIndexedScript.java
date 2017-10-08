package io.searchbox.indices.script;

/**
 * @author cihat keser
 */
public class GetIndexedScript extends AbstractIndexedScript {
    protected GetIndexedScript(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractIndexedScript.Builder<GetIndexedScript, Builder> {

        public Builder(String scriptName) {
            super(scriptName);
        }

        @Override
        public GetIndexedScript build() {
            return new GetIndexedScript(this);
        }

    }
}
