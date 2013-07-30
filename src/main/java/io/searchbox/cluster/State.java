package io.searchbox.cluster;

import io.searchbox.AbstractAction;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class State extends AbstractAction {

    public State(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_cluster/state");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractAction.Builder<State, Builder> {

        /**
         * Set to true to filter out the routing_table part of the response.
         */
        public Builder filterRoutingTable(boolean value) {
            return setParameter("filter_routing_table", value);
        }

        /**
         * Set to true to filter out the metadata part of the response.
         */
        public Builder filterMetadata(boolean value) {
            return setParameter("filter_metadata", value);
        }

        /**
         * Set to true to filter out the blocks part of the response.
         */
        public Builder filterBlocks(boolean value) {
            return setParameter("filter_blocks", value);
        }

        /**
         * When not filtering metadata, a comma separated list of indices to include in the response.
         */
        public Builder filterIndices(String value) {
            return setParameter("filter_indices", value);
        }

        /**
         * Set to true to filter out the nodes part of the response.
         */
        public Builder filterNodes(boolean value) {
            return setParameter("filter_nodes", value);
        }

        /**
         * For debugging purposes, you can retrieve the cluster state local to a particular node.
         */
        public Builder local(boolean value) {
            return setParameter("local", value);
        }

        @Override
        public State build() {
            return new State(this);
        }
    }
}
