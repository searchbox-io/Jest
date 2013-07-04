package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;

/**
 * @author Dogukan Sonmez
 */
public class IndicesExists extends AbstractAction {

    public IndicesExists(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "HEAD";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<IndicesExists, Builder> {

        @Override
        public IndicesExists build() {
            return new IndicesExists(this);
        }
    }

}
