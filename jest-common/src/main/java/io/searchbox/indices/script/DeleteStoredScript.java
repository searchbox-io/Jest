package io.searchbox.indices.script;

public class DeleteStoredScript extends AbstractStoredScript {

    protected DeleteStoredScript(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    public static class Builder extends AbstractStoredScript.Builder<DeleteStoredScript, Builder> {

        public Builder(String scriptName) {
            super(scriptName);
        }

        @Override
        public DeleteStoredScript build() {
            return new DeleteStoredScript(this);
        }

    }
}
