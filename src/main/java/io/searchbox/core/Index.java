package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.searchbox.AbstractDocumentTargetedAction;
import io.searchbox.BulkableAction;
import io.searchbox.params.Parameters;

import java.util.Collection;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Index extends AbstractDocumentTargetedAction implements BulkableAction {

    private Object source;

    private Index(Builder builder) {
        super(builder);

        this.source = builder.source;
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
    public Object getData(Gson gson) {
        return source;
    }

    @Override
    public String getBulkMethodName() {
        Collection<Object> opType = getParameter(Parameters.OP_TYPE);
        if (opType != null) {
            if (opType.size() > 1) {
                throw new IllegalArgumentException("Expecting a single value for OP_TYPE parameter, you provided: " + opType.size());
            }
            return (opType.size() == 1 && ((opType.iterator().next()).toString().equalsIgnoreCase("create"))) ? "create" : "index";
        } else {
            return "index";
        }
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
