package io.searchbox.core;

import com.google.gson.JsonObject;
import io.searchbox.AbstractAction;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */


public class Index extends AbstractAction {

    final static Logger log = LoggerFactory.getLogger(Index.class);

    public static class Builder {
        private String index = null;
        private String type = null;
        private String id = null;
        private final Object source;

        public Builder(Object source) {
            this.source = source;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Index build() {
            return new Index(this);
        }
    }

    private Index(Builder builder) {
        setData(builder.source);
        String id = StringUtils.isNotBlank(builder.id) ? builder.id : this.getIdFromSource(builder.source);
        prepareIndex(builder.index, builder.type, id);
        setURI(buildURI());
    }

    private void prepareIndex(String indexName, String typeName, String id) {
        this.indexName = indexName;
        this.typeName = typeName;
        this.id = id;
    }

    @Override
    public String getName() {
        return "INDEX";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }


    @Override
    public Boolean isOperationSucceed(JsonObject result) {
        return result.get("ok").getAsBoolean();
    }

    @Override
    public String getRestMethodName() {
        return (id != null) ? "PUT" : "POST";
    }
}
