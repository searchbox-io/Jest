package io.searchbox.core;

import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

public class Ping extends GenericResultAbstractAction {
    protected Ping(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion);
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends GenericResultAbstractAction.Builder<Ping, Builder> {
        public Ping build() {
            return new Ping(this);
        }
    }
}
