package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;
import io.searchbox.Action;
import io.searchbox.params.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ferhat
 */
public class SearchScroll extends AbstractAction implements Action {

    final static Logger log = LoggerFactory.getLogger(SearchScroll.class);

    public SearchScroll(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_search/scroll");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    public String getPathToResult() {
        return "hits/hits/_source";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<SearchScroll, Builder> {

        public Builder(String scrollId, String scroll) {
            setParameter(Parameters.SCROLL_ID, scrollId);
            setParameter(Parameters.SCROLL, scroll);
        }

        @Override
        public SearchScroll build() {
            return new SearchScroll(this);
        }
    }
}
