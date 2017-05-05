package io.searchbox.core;

import io.searchbox.action.BulkableAction;
import io.searchbox.action.SingleResultAbstractDocumentTargetedAction;
import io.searchbox.params.Parameters;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Update extends SingleResultAbstractDocumentTargetedAction implements BulkableAction<DocumentResult> {

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

    public static class Builder extends SingleResultAbstractDocumentTargetedAction.Builder<Update, Builder> {
        private final Object payload;

        public Builder(Object payload) {
            this.payload = payload;
        }

        public Update build() {
            return new Update(this);
        }
    }

    public static class VersionBuilder extends Builder {
        public VersionBuilder(Object payload, Long version) {
            super(payload);
            this.setParameter(Parameters.VERSION, version);
        }
    }
}
