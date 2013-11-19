package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;

/**
 * @author ferhat
 * @author cihat keser
 */
public class Status extends AbstractAction {

    private Status(Builder builder) {
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
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_status");
        return sb.toString();
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Status, Builder> {

        @Override
        public Status build() {
            return new Status(this);
        }
    }
}