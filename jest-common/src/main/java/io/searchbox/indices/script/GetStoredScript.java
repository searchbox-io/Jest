package io.searchbox.indices.script;

/**
 * @author cihat keser
 */
public class GetStoredScript extends AbstractStoredScript {
    protected GetStoredScript(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractStoredScript.Builder<GetStoredScript, Builder> {

        public Builder(String scriptName) {
            super(scriptName);
        }

        @Override
        public GetStoredScript build() {
            return new GetStoredScript(this);
        }

    }
}
