package io.searchbox.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sofiane Cherchalli
 */
public class SearchTemplate extends Search {

    final static Logger log = LoggerFactory.getLogger(SearchTemplate.class);

    protected SearchTemplate(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_search/template");
        return sb.toString();
    }

    public static class Builder extends Search.Builder {

        public Builder(String query) {
            super(query);
        }

        @Override
        public SearchTemplate build() {
            return new SearchTemplate(this);
        }
    }
}
