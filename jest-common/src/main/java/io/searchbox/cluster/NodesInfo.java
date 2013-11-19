package io.searchbox.cluster;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiINodeActionBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class NodesInfo extends AbstractAction {

    public NodesInfo(Builder builder) {
        super(builder);
        setPathToResult("nodes");
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_cluster/nodes")
                .append("/")
                .append(nodes);
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractMultiINodeActionBuilder<NodesInfo, Builder> {

        public Builder settings(boolean value) {
            return setParameter("settings", value);
        }

        public Builder os(boolean value) {
            return setParameter("os", value);
        }

        public Builder process(boolean value) {
            return setParameter("process", value);
        }

        public Builder jvm(boolean value) {
            return setParameter("jvm", value);
        }

        public Builder threadPool(boolean value) {
            return setParameter("thread_pool", value);
        }

        public Builder network(boolean value) {
            return setParameter("network", value);
        }

        public Builder transport(boolean value) {
            return setParameter("transport", value);
        }

        public Builder http(boolean value) {
            return setParameter("http", value);
        }

        public Builder plugin(boolean value) {
            return setParameter("plugin", value);
        }

        @Override
        public NodesInfo build() {
            return new NodesInfo(this);
        }
    }
}

