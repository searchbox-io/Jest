package io.searchbox.indices.script;

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
