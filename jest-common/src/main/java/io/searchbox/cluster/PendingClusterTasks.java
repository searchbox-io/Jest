package io.searchbox.cluster;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;

public class PendingClusterTasks extends GenericResultAbstractAction {
    protected PendingClusterTasks(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_cluster/pending_tasks";
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
