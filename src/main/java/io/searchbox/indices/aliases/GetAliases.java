package io.searchbox.indices.aliases;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;

/**
 * @author cihat keser
 */
public class GetAliases extends AbstractAction {

    private GetAliases() {
    }

    private GetAliases(Builder builder) {
        this.indexName = builder.getJoinedIndices();
        setURI(buildURI());
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<GetAliases, Builder> {

        @Override
        public GetAliases build() {
            return new GetAliases(this);
        }
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_aliases");
        return sb.toString();
    }
}
