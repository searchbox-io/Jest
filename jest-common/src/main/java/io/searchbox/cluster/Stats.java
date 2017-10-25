package io.searchbox.cluster;

import io.searchbox.action.AbstractMultiINodeActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

public class Stats extends GenericResultAbstractAction {
    protected Stats(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_cluster/stats/nodes/" + nodes;
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
