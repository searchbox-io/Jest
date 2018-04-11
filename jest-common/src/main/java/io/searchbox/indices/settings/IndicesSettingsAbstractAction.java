package io.searchbox.indices.settings;

import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author cihat keser
 */
public abstract class IndicesSettingsAbstractAction extends GenericResultAbstractAction {

    protected IndicesSettingsAbstractAction(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_settings";
    }

}
