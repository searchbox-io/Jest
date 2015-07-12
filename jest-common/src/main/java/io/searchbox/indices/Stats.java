package io.searchbox.indices;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;

import java.util.Arrays;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Stats extends GenericResultAbstractAction {

    protected Stats(Builder builder) {
        super(builder);
        indexName = builder.getJoinedIndices();

        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_stats";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Stats, Builder> {

        public Builder clear(boolean clear) {
            return setParameter("clear", clear);
        }

        public Builder docs(boolean docs) {
            return setParameter("docs", docs);
        }

        public Builder store(boolean store) {
            return setParameter("store", store);
        }

        public Builder indexing(boolean indexing) {
            return setParameter("indexing", indexing);
        }

        public Builder indexing(boolean indexing, String... types) {
            setParameter("indexing", indexing);
            setParameter("types", Arrays.asList(types));
            return this;
        }

        public Builder get(boolean get) {
            return setParameter("get", get);
        }

        public Builder warmer(boolean warmer) {
            return setParameter("warmer", warmer);
        }

        public Builder merge(boolean merge) {
            return setParameter("merge", merge);
        }

        public Builder flush(boolean flush) {
            return setParameter("flush", flush);
        }

        public Builder refresh(boolean refresh) {
            return setParameter("refresh", refresh);
        }

        public Builder search(boolean search) {
            return setParameter("search", search);
        }

        // TODO add "search with groups" parameter

        @Override
        public Stats build() {
            return new Stats(this);
        }
    }
}