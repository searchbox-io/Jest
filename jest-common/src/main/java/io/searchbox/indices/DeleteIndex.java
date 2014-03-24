package io.searchbox.indices;

import com.google.gson.JsonObject;
import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class DeleteIndex extends GenericResultAbstractAction {

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
        //TODO check http header
        //return (result.get("ok").getAsBoolean() && result.get("acknowledged").getAsBoolean());
        return true;
    }

    public static class Builder extends GenericResultAbstractAction.Builder<DeleteIndex, Builder> {
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
