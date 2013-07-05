package io.searchbox.indices;

import io.searchbox.AbstractAction;

/**
 * @author cihat keser
 */
public class CloseIndex extends AbstractAction {

    public CloseIndex(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_close");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends AbstractAction.Builder<CloseIndex, Builder> {
        private String index;

        public Builder(String index) {
            this.index = index;
        }

        @Override
        public CloseIndex build() {
            return new CloseIndex(this);
        }
    }
}
