package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Refresh extends AbstractAction {

    private Refresh() {
    }

    private Refresh(Builder builder) {
        this.indexName = builder.getJoinedIndices();
        setURI(buildURI());
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Refresh, Builder> {

        @Override
        public Refresh build() {
            return new Refresh(this);
        }
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_refresh");
        return sb.toString();
    }
}
