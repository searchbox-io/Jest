package io.searchbox.indices.mapping;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiTypeActionBuilder;

/**
 * @author ferhat
 * @author cihat keser
 */
public class GetMapping extends AbstractAction {

    private GetMapping(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_mapping");
        return sb.toString();
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<GetMapping, Builder> {

        @Override
        public GetMapping build() {
            return new GetMapping(this);
        }
    }

}
