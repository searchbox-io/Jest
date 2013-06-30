package io.searchbox.core;

import com.google.gson.JsonObject;
import io.searchbox.AbstractAction;
import io.searchbox.AbstractDocumentTargetedAction;
import io.searchbox.BulkableAction;
import io.searchbox.params.Parameters;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Index extends AbstractDocumentTargetedAction implements BulkableAction {

    final static Logger log = LoggerFactory.getLogger(Index.class);

    private Index(Builder builder) {
        super(builder);
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

    @Override
    public String getBulkMethodName() {
        return (getParameter(Parameters.OP_TYPE) != null && ((String) getParameter(Parameters.OP_TYPE)).equalsIgnoreCase("create")) ? "create" : "index";
    }

    public static class Builder extends AbstractAction.Builder<Index, Builder> {
        private final Object source;
        private String index;
        private String type;
        private String id;

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
}
