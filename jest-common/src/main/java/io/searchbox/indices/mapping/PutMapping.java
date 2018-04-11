package io.searchbox.indices.mapping;

import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author ferhat
 * @author cihat keser
 */
public class PutMapping extends GenericResultAbstractAction {

    protected PutMapping(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        this.typeName = builder.type;
        this.payload = builder.source;
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_mapping";
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    public static class Builder extends GenericResultAbstractAction.Builder<PutMapping, Builder> {
        private String index;
        private String type;
        private Object source;

        public Builder(String index, String type, Object source) {
            this.index = index;
            this.type = type;
            this.source = source;
        }

        @Override
        public PutMapping build() {
            return new PutMapping(this);
        }
    }

}
