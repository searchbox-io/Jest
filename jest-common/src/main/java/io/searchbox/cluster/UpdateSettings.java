package io.searchbox.cluster;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * Allows to update cluster wide specific settings. Settings updated can either be persistent (applied cross restarts)
 * or transient (will not survive a full cluster restart). The cluster responds with the settings updated.
 * <br/>
 * <br/>
 * There is a specific list of settings that can be updated, please see
 * <a href="http://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-update-settings.html#cluster-settings">Elasticsearch docs</a>
 * for more information.
 *
 * @author cihat keser
 */
public class UpdateSettings extends GenericResultAbstractAction {

    protected UpdateSettings(Builder builder) {
        super(builder);
        this.payload = builder.source;
    }

    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_cluster/settings";
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    public static class Builder extends AbstractAction.Builder<UpdateSettings, Builder> {
        private final Object source;

        /**
         * There is a specific list of settings that can be updated, please see
         * <a href="http://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-update-settings.html#cluster-settings">Elasticsearch docs</a>
         * for more information.
         */
        public Builder(Object source) {
            this.source = source;
        }

        @Override
        public UpdateSettings build() {
            return new UpdateSettings(this);
        }
    }

}
