package io.searchbox.cluster;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiINodeActionBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class NodesShutdown extends AbstractAction {

    public NodesShutdown(Builder builder) {
        super(builder);
        setPathToResult("nodes");
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_cluster/nodes/")
                .append(nodes)
                .append("/_shutdown");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends AbstractMultiINodeActionBuilder<NodesShutdown, Builder> {

        /**
         * By default, the shutdown will be executed after a 1 second delay (1s).
         * The delay can be customized by setting the delay parameter in a time value format.
         *
         * @param value e.g.: "1s" -> 1 second, "10m" -> 10 minutes
         */
        public Builder delay(String value) {
            return setParameter("delay", value);
        }

        @Override
        public NodesShutdown build() {
            return new NodesShutdown(this);
        }
    }
}