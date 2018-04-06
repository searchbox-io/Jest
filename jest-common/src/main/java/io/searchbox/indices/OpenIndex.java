package io.searchbox.indices;

import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author cihat keser
 */
public class OpenIndex extends GenericResultAbstractAction {

    protected OpenIndex(Builder builder) {
        super(builder);
        this.indexName = builder.index;
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_open";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends GenericResultAbstractAction.Builder<OpenIndex, Builder> {
        private String index;

        public Builder(String index) {
            this.index = index;
        }

        @Override
        public OpenIndex build() {
            return new OpenIndex(this);
        }
    }
}
