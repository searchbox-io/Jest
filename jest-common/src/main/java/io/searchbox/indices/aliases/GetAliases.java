package io.searchbox.indices.aliases;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author cihat keser
 */
public class GetAliases extends GenericResultAbstractAction {

    private GetAliases(Builder builder) {
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
        sb.append(super.buildURI()).append("/_aliases");
        return sb.toString();
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<GetAliases, Builder> {

        @Override
        public GetAliases build() {
            return new GetAliases(this);
        }
    }
}
