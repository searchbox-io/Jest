package io.searchbox.indices;

import com.google.common.base.Joiner;
import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Stats extends GenericResultAbstractAction {

    protected Stats(Builder builder) {
        super(builder);
        indexName = builder.getJoinedIndices();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_stats";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Stats, Builder> {

        public Builder clear(boolean clear) {
            return setParameter("clear", clear);
        }

        public Builder docs(boolean docs) {
            return toggleApiParameter("docs", docs);
        }

        public Builder store(boolean store) {
            return toggleApiParameter("store", store);
        }

        public Builder indexing(boolean indexing) {
            return toggleApiParameter("indexing", indexing);
        }

        public Builder indexing(boolean indexing, String... types) {
            toggleApiParameter("indexing", indexing);
            setParameter("types", Joiner.on(",").join(types));
            return this;
        }

        public Builder get(boolean get) {
            return toggleApiParameter("get", get);
        }

        public Builder warmer(boolean warmer) {
            return toggleApiParameter("warmer", warmer);
        }

        public Builder merge(boolean merge) {
            return toggleApiParameter("merge", merge);
        }

        public Builder flush(boolean flush) {
            return toggleApiParameter("flush", flush);
        }

        public Builder refresh(boolean refresh) {
            return toggleApiParameter("refresh", refresh);
        }

        public Builder search(boolean search) {
            return toggleApiParameter("search", search);
        }

        public Builder search(boolean search, String... groups) {
            toggleApiParameter("search", search);
            setParameter("groups", Joiner.on(",").join(groups));
            return this;
        }

        public Builder completion(boolean completion) {
            return toggleApiParameter("completion", completion);
        }

        public Builder fielddata(boolean fielddata) {
            return toggleApiParameter("fielddata", fielddata);
        }

        public Builder requestCache(boolean requestCache) {
            return toggleApiParameter("request_cache", requestCache);
        }

        public Builder suggest(boolean suggest) {
            return toggleApiParameter("suggest", suggest);
        }

        public Builder translog(boolean translog) {
            return toggleApiParameter("translog", translog);
        }

        // TODO add "search with groups" parameter

        @Override
        public Stats build() {
            return new Stats(this);
        }
    }
}