package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Stats extends AbstractAction {

    private Stats() {
    }

    private Stats(Builder builder) {
        this.indexName = builder.getJoinedIndices();
        this.addParameter(builder.parameters);

        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    public String getURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_stats");
        return sb.toString();
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Stats, Builder> {

        private Map<String, Object> parameters = new HashMap<String, Object>();

        public Builder clear(boolean clear) {
            parameters.put("clear", clear);
            return this;
        }

        public Builder docs(boolean docs) {
            parameters.put("docs", docs);
            return this;
        }

        public Builder store(boolean store) {
            parameters.put("store", store);
            return this;
        }

        public Builder indexing(boolean indexing) {
            parameters.put("indexing", indexing);
            parameters.remove("types");
            return this;
        }

        public Builder indexing(boolean indexing, String... types) {
            parameters.put("indexing", indexing);
            parameters.put("types", Arrays.asList(types));
            return this;
        }

        public Builder get(boolean get) {
            parameters.put("get", get);
            return this;
        }

        public Builder warmer(boolean warmer) {
            parameters.put("warmer", warmer);
            return this;
        }

        public Builder merge(boolean merge) {
            parameters.put("merge", merge);
            return this;
        }

        public Builder flush(boolean flush) {
            parameters.put("flush", flush);
            return this;
        }

        public Builder refresh(boolean refresh) {
            parameters.put("refresh", refresh);
            return this;
        }

        public Builder search(boolean search) {
            parameters.put("search", search);
            return this;
        }

        // TODO add "search with groups" parameter

        @Override
        public Stats build() {
            return new Stats(this);
        }
    }
}