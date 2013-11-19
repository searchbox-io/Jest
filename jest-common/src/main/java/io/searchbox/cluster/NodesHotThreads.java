package io.searchbox.cluster;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiINodeActionBuilder;

/**
 * Allows to get the current hot threads on each node in the cluster.
 * <b>This API is experimental.</b>
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class NodesHotThreads extends AbstractAction {

    public NodesHotThreads(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_cluster/nodes/")
                .append(nodes)
                .append("/hot_threads");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractMultiINodeActionBuilder<NodesHotThreads, Builder> {

        public Builder settings(boolean value) {
            return setParameter("settings", value);
        }

        /**
         * number of hot threads to provide, defaults to 3
         */
        public Builder threads(Integer value) {
            return setParameter("threads", value);
        }

        /**
         * the interval to do the second sampling of threads. Defaults to 500ms
         */
        public Builder interval(String value) {
            return setParameter("interval", value);
        }

        /**
         * The type to sample, defaults to cpu, but supports wait and block to see hot threads that are in wait or block state
         */
        public Builder type(String value) {
            return setParameter("type", value);
        }

        @Override
        public NodesHotThreads build() {
            return new NodesHotThreads(this);
        }
    }
}
