package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Flush extends AbstractAction {

    private Flush() {
    }

    private Flush(Builder builder) {
        this.indexName = builder.getJoinedIndices();
        this.addParameter("refresh", builder.refresh);

        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_flush");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Flush, Builder> {
        private boolean refresh;

        public Builder refresh(boolean refresh) {
            this.refresh = refresh;
            return this;
        }

        @Override
        public Flush build() {
            return new Flush(this);
        }
    }
}
