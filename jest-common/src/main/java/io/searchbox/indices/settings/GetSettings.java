package io.searchbox.indices.settings;

import io.searchbox.action.AbstractMultiIndexActionBuilder;

/**
 * The get settings API allows to retrieve settings of index/indices.
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class GetSettings extends IndicesSettingsAbstractAction {

    protected GetSettings(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "GET";
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
