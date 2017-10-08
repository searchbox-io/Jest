package io.searchbox.indices;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author ferhat
 * @author cihat keser
 */
public class Status extends GenericResultAbstractAction {

    protected Status(Builder builder) {
        super(builder);
        this.indexName = builder.getJoinedIndices();
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_status";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Status, Builder> {

        @Override
        public Status build() {
            return new Status(this);
        }
    }
}