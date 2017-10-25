package io.searchbox.cluster;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

public class PendingClusterTasks extends GenericResultAbstractAction {
    protected PendingClusterTasks(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_cluster/pending_tasks";
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractAction.Builder<PendingClusterTasks, Builder> {
        @Override
        public PendingClusterTasks build() {
            return new PendingClusterTasks(this);
        }
    }
}
