package io.searchbox.cluster;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class State extends GenericResultAbstractAction {

    protected State(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_cluster/state";
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractAction.Builder<State, Builder> {

        /**
         * Shows the cluster state version.
         */
        public Builder withVersion() {
            return addCleanApiParameter("version");
        }

        /**
         * Shows the elected master_node part of the response.
         */
        public Builder withMasterNode() {
            return addCleanApiParameter("master_node");
        }

        /**
         * Shows the nodes part of the response
         */
        public Builder withNodes() {
            return addCleanApiParameter("nodes");
        }

        /**
         * Shows the routing_table part of the response. 
         */
        public Builder withRoutingTable() {
            return addCleanApiParameter("routing_table");
        }

        /**
         * Shows the metadata part of the response.
         */
        public Builder withMetadata() {
            return addCleanApiParameter("metadata");
        }

        /**
         * Shows the blocks part of the response
         */
        public Builder withBlocks() {
            return addCleanApiParameter("blocks");
        }

        /**
         * When not filtering metadata, a comma separated list of indices to include in the response.
         */
        public Builder indices(String value) {
            return setParameter("indices", value);
        }

        /**
         * For debugging purposes, you can retrieve the cluster state local to a particular node.
         */
        public Builder local() {
            return addCleanApiParameter("local");
        }

        @Override
        public State build() {
            return new State(this);
        }
    }
}
