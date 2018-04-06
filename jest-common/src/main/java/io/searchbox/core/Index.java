package io.searchbox.core;

import io.searchbox.action.BulkableAction;
import io.searchbox.action.SingleResultAbstractDocumentTargetedAction;
import io.searchbox.params.Parameters;

import java.util.Collection;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Index extends SingleResultAbstractDocumentTargetedAction implements BulkableAction<DocumentResult> {

    protected Index(Builder builder) {
        super(builder);
        this.payload = builder.source;
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public String getRestMethodName() {
        return (id != null) ? "PUT" : "POST";
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

    public static class Builder extends SingleResultAbstractDocumentTargetedAction.Builder<Index, Builder> {
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
