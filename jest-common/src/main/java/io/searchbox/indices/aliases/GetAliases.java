package io.searchbox.indices.aliases;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author cihat keser
 */
public class GetAliases extends GenericResultAbstractAction {

    protected GetAliases(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_aliases";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<GetAliases, Builder> {

        @Override
        public GetAliases build() {
            return new GetAliases(this);
        }
    }
}
