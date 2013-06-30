package io.searchbox.core;

import com.google.gson.JsonObject;
import io.searchbox.AbstractDocumentTargetedAction;
import io.searchbox.BulkableAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Update extends AbstractDocumentTargetedAction implements BulkableAction {

    final static Logger log = LoggerFactory.getLogger(Update.class);

    private Update(Builder builder) {
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
        setData(builder.payload);
        setURI(buildURI());
    }

    @Override
    public String getBulkMethodName() {
        return "update";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_update");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public Boolean isOperationSucceed(JsonObject result) {
        return result.get("ok").getAsBoolean();
    }

    public static class Builder {
        private final Object payload;
        private String index;
        private String type;
        private String id = null;

        public Builder(Object payload) {
            this.payload = payload;
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Update build() {
            return new Update(this);
        }
    }

}
