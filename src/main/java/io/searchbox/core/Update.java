package io.searchbox.core;

import com.google.gson.JsonObject;
import io.searchbox.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 */
public class Update extends AbstractAction {

    final static Logger log = LoggerFactory.getLogger(Update.class);

    public static class Builder {
        private String index;
        private String type;
        private String id = null;
        private final Object script;

        public Builder(Object script) {
            this.script = script;
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

    private Update(Builder builder) {
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
        setData(builder.script);
        setURI(buildURI());
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

}
