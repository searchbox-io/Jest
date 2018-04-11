package io.searchbox.core;

import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Validate extends GenericResultAbstractAction {

    protected Validate(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        this.typeName = builder.type;
        this.payload = builder.query;
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_validate/query";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getPathToResult() {
        return "valid";
    }

    public static class Builder extends GenericResultAbstractAction.Builder<Validate, Builder> {
        private final Object query;
        private String index;
        private String type;

        public Builder(Object query) {
            this.query = query;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Validate build() {
            return new Validate(this);
        }
    }
}
