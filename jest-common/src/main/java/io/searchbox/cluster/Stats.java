package io.searchbox.cluster;

import io.searchbox.action.AbstractMultiINodeActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;

public class Stats extends GenericResultAbstractAction {
    protected Stats(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_cluster/stats/nodes/" + nodes;
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractMultiINodeActionBuilder<Stats, Builder> {
        @Override
        public Stats build() {
            return new Stats(this);
        }
    }
}
