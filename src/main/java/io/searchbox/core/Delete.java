package io.searchbox.core;

import com.google.gson.JsonObject;
import io.searchbox.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */


public class Delete extends AbstractAction {

    final static Logger log = LoggerFactory.getLogger(Delete.class);

    public static class Builder {
        private String index = null;
        private String type = null;
        private String id = null;

        public Builder() {
        }

        public Builder(String id) {
            this.id = id;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Delete build() {
            return new Delete(this);
        }
    }

    private Delete(Builder builder) {
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
        setURI(buildURI());
    }

    @Override
    public String getName() {
        return "DELETE";
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public Boolean isOperationSucceed(JsonObject result) {
        return (result.get("ok").getAsBoolean() && result.get("found").getAsBoolean());
    }
}
