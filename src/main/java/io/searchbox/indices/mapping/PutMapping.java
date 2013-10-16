package io.searchbox.indices.mapping;

import com.google.gson.Gson;
import io.searchbox.AbstractAction;

/**
 * @author ferhat
 * @author cihat keser
 */
public class PutMapping extends AbstractAction {

    private Object source;

    public PutMapping(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        this.typeName = builder.type;
        this.source = builder.source;
        setURI(buildURI());
    }

    @Override
    public Object getData(Gson gson) {
        return source;
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_mapping");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    public static class Builder extends AbstractAction.Builder<PutMapping, Builder> {
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
