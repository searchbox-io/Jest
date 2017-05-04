package io.searchbox.indices.mapping;

import io.searchbox.action.GenericResultAbstractAction;

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
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_mapping";
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
