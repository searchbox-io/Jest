package io.searchbox.indices;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Flush extends GenericResultAbstractAction {

    protected Flush(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_flush";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Flush, Builder> {
        public Builder force(boolean force) {
            return setParameter("force", force);
        }

        public Builder force() {
            return force(true);
        }

        public Builder waitIfOngoing(boolean waitIfOngoing) {
            return setParameter("wait_if_ongoing", waitIfOngoing);
        }

        public Builder waitIfOngoing() {
            return waitIfOngoing(true);
        }

        @Override
        public Flush build() {
            return new Flush(this);
        }
    }
}
