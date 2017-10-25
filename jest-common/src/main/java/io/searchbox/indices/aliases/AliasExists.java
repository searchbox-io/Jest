package io.searchbox.indices.aliases;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

public class AliasExists extends GenericResultAbstractAction {
    private String alias;

    protected AliasExists(Builder builder, String alias) {
        super(builder);
        this.alias = alias;
    }

    @Override
    public String getRestMethodName() {
        return "HEAD";
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_alias/" + alias;
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<AliasExists, Builder> {
        protected String alias = "*";

        public Builder alias(String alias) {
            this.alias = alias;
            return this;
        }

        @Override
        public AliasExists build() {
            return new AliasExists(this, alias);
        }
    }
}
