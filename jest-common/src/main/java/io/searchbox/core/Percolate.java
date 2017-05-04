package io.searchbox.core;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * Use this action to query on registered percolaters.
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Percolate extends GenericResultAbstractAction {

    protected Percolate(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        this.typeName = builder.type;
        this.payload = builder.query;
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_percolate";
    }

    public static class Builder extends GenericResultAbstractAction.Builder<Percolate, Builder> {
        private String index;
        private String type;
        private Object query;

        public Builder(String index, String type, Object query) {
            this.index = index;
            this.type = type;
            this.query = query;
        }

        @Override
        public Percolate build() {
            return new Percolate(this);
        }
    }
}
