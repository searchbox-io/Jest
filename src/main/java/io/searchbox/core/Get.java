package io.searchbox.core;

import com.google.gson.JsonObject;
import io.searchbox.AbstractDocumentTargetedAction;


/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Get extends AbstractDocumentTargetedAction {

    private Get(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    public String getPathToResult() {
        return "_source";
    }

    @Override
    public Boolean isOperationSucceed(JsonObject result) {
        return result.get("exists").getAsBoolean();
    }

    public static class Builder extends AbstractDocumentTargetedAction.Builder<Get, Builder> {

        public Builder(String id) {
            this.id(id);
        }

        public Get build() {
            return new Get(this);
        }
    }
}
