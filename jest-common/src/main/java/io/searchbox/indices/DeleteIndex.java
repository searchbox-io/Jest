package io.searchbox.indices;

import com.google.gson.JsonObject;
import io.searchbox.AbstractAction;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class DeleteIndex extends AbstractAction {

    public DeleteIndex(Builder builder) {
        super(builder);
        indexName = builder.index;
        typeName = builder.type;
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    @Override
    public Boolean isOperationSucceed(JsonObject result) {
        return (result.get("ok").getAsBoolean() && result.get("acknowledged").getAsBoolean());
    }

    public static class Builder extends AbstractAction.Builder<DeleteIndex, Builder> {
        private String index;
        private String type;

        public Builder(String index) {
            this.index = index;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        @Override
        public DeleteIndex build() {
            return new DeleteIndex(this);
        }
    }

}
