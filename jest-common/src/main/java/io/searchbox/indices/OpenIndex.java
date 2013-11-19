package io.searchbox.indices;

import io.searchbox.AbstractAction;

/**
 * @author cihat keser
 */
public class OpenIndex extends AbstractAction {

    public OpenIndex(Builder builder) {
        super(builder);
        this.indexName = builder.index;
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_open");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends AbstractAction.Builder<OpenIndex, Builder> {
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
