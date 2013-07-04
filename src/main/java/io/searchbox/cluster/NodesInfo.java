package io.searchbox.cluster;

import io.searchbox.AbstractAction;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class NodesInfo extends AbstractAction {

    private Collection<String> nodes;

    public NodesInfo(Builder builder) {
        super(builder);
        this.nodes = builder.nodes;
        setPathToResult("nodes");
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_cluster/nodes");
        if (this.nodes.size() > 0) {
            sb.append("/").append(StringUtils.join(this.nodes, ","));
        }
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractAction.Builder<NodesInfo, Builder> {

        private Collection<String> nodes = new LinkedList<String>();

        public Builder addNode(String node) {
            nodes.add(node);
            return this;
        }

        public Builder addNode(Collection<? extends String> nodes) {
            this.nodes.addAll(nodes);
            return this;
        }

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

