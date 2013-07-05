package io.searchbox.core;

import io.searchbox.AbstractAction;

/**
 * Use this action to query on registered percolaters.
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Percolate extends AbstractAction {

    public Percolate(Builder builder) {
        super(builder);
        this.indexName = builder.index;
        this.typeName = builder.type;
        setURI(buildURI());
        setData(builder.query);
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_percolate");
        return sb.toString();
    }

    public static class Builder extends AbstractAction.Builder<Percolate, Builder> {
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
