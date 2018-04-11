package io.searchbox.indices;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Refresh extends GenericResultAbstractAction {

    protected Refresh(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_refresh";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Refresh, Builder> {

        @Override
        public Refresh build() {
            return new Refresh(this);
        }
    }
}
