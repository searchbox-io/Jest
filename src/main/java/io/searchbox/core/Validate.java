package io.searchbox.core;

import io.searchbox.AbstractAction;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Validate extends AbstractAction {

    private Validate(Builder builder) {
        super(builder);
        this.indexName = builder.index;
        this.typeName = builder.type;
        setURI(buildURI());
        setData(builder.query);
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_validate/query");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getPathToResult() {
        return "valid";
    }

    public static class Builder extends AbstractAction.Builder<Validate, Builder> {
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
