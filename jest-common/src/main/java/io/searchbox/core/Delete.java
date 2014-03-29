package io.searchbox.core;

import io.searchbox.action.AbstractDocumentTargetedAction;
import io.searchbox.action.BulkableAction;
import io.searchbox.action.GenericResultAbstractDocumentTargetedAction;
import io.searchbox.client.JestResult;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Delete extends GenericResultAbstractDocumentTargetedAction implements BulkableAction<JestResult> {

    private Delete(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public String getBulkMethodName() {
        return "delete";
    }

    public static class Builder extends AbstractDocumentTargetedAction.Builder<Delete, Builder> {

        public Builder(String id) {
            this.id(id);
        }

        public Delete build() {
            return new Delete(this);
        }

    }
}
