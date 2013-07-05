package io.searchbox.core;

import com.google.gson.JsonObject;
import io.searchbox.AbstractDocumentTargetedAction;
import io.searchbox.BulkableAction;
import io.searchbox.params.Parameters;
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
        setURI(buildURI());
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

    public static class Builder extends AbstractDocumentTargetedAction.Builder<Index, Builder> {
        private final Object source;

        public Builder(Object source) {
            this.source = source;
            this.id(getIdFromSource(source)); // set the default for id if it exists in source
        }

        public Index build() {
            return new Index(this);
        }
    }
}
