package io.searchbox.core;

import com.google.gson.JsonObject;
import io.searchbox.AbstractAction;


/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */


public class Get extends AbstractAction {

    public static class Builder {
        private String index = null;
        private String type = null;
        private final String id;

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

        public Get build() {
            return new Get(this);
        }
    }

    private Get(Builder builder) {
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
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
}
