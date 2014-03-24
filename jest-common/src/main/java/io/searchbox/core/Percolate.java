package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.GenericResultAbstractAction;

/**
 * Use this action to query on registered percolaters.
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Percolate extends GenericResultAbstractAction {

    private Object query;

    public Percolate(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        this.typeName = builder.type;
        this.query = builder.query;
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public Object getData(Gson gson) {
        return query;
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_percolate");
        return sb.toString();
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
