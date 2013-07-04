package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Refresh extends AbstractAction {

    private Refresh(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_refresh");
        return sb.toString();
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Refresh, Builder> {

        @Override
        public Refresh build() {
            return new Refresh(this);
        }
    }
}
