package io.searchbox.indices.settings;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class GetSettings extends GenericResultAbstractAction {

    private GetSettings(Builder builder) {
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
        sb.append(super.buildURI()).append("/_settings");
        return sb.toString();
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<GetSettings, Builder> {

        @Override
        public GetSettings build() {
            return new GetSettings(this);
        }

        /**
         * Prefix Query Option allows to include only settings (whose keys) matches the specified prefix.
         */
        public Builder prefixQuery(String prefixQuery) {
            return setParameter("prefix", prefixQuery);
        }

    }

}
