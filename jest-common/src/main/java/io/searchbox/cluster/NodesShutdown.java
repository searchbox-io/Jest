package io.searchbox.cluster;

import io.searchbox.action.AbstractMultiINodeActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class NodesShutdown extends GenericResultAbstractAction {

    protected NodesShutdown(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_nodes/" +
                nodes +
                "/_shutdown";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getPathToResult() {
        return "nodes";
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