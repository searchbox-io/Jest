package io.searchbox.core;

import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * The search shards api returns the indices and shards that a search request would be executed against.
 * This can give useful feedback for working out issues or planning optimizations with routing and shard preferences.
 *
 * @author cihat keser
 */
public class SearchShards extends GenericResultAbstractAction {

    protected SearchShards(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_search_shards";
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<SearchShards, Builder> {

        /**
         *
         * @param routing A comma-separated list of routing values to take into account when
         *                determining which shards a request would be executed against.
         */
        public Builder routing(String routing) {
            setParameter("routing", routing);
            return this;
        }

        /**
         *
         * @param preference Controls a preference of which shard replicas to execute the search request on.
         *                   By default, the operation is randomized between the shard replicas. See the
         *                   <a href="http://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-preference.html">preference documentation</a>
         *                   for a list of all acceptable values.
         */
        public Builder preference(String preference) {
            setParameter("preference", preference);
            return this;
        }

        /**
         *
         * @param local A boolean value whether to read the cluster state locally in order to determine
         *              where shards are allocated instead of using the Master node’s cluster state.
         */
        public Builder local(Boolean local) {
            setParameter("local", local);
            return this;
        }

        @Override
        public SearchShards build() {
            return new SearchShards(this);
        }
    }
}

