package io.searchbox.core;

import io.searchbox.action.BulkableAction;
import io.searchbox.action.SingleResultAbstractDocumentTargetedAction;
import io.searchbox.params.Parameters;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Index extends SingleResultAbstractDocumentTargetedAction implements BulkableAction<DocumentResult> {

    protected Index(Builder builder) {
        super(builder);

        this.payload = builder.source;
        setURI(buildURI());
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .isEquals();
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
