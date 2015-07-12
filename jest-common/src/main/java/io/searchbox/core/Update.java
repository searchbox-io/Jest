package io.searchbox.core;

import io.searchbox.action.BulkableAction;
import io.searchbox.action.GenericResultAbstractDocumentTargetedAction;
import io.searchbox.client.JestResult;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Update extends GenericResultAbstractDocumentTargetedAction implements BulkableAction<JestResult> {

    protected Update(Builder builder) {
        super(builder);

        this.payload = builder.payload;
        setURI(buildURI());
    }

    @Override
    public String getBulkMethodName() {
        return "update";
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_update";
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

    public static class Builder extends GenericResultAbstractDocumentTargetedAction.Builder<Update, Builder> {
        private final Object payload;

        public Builder(Object payload) {
            this.payload = payload;
        }

        public Update build() {
            return new Update(this);
        }
    }

}
