package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;
import io.searchbox.Action;
import io.searchbox.params.Parameters;
import org.apache.commons.lang3.StringUtils;

/**
 * @author ferhat
 */
public class SearchScroll extends AbstractAction implements Action {

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
        public String getJoinedIndices() {
            if (indexNames.size() > 0) {
                return StringUtils.join(indexNames, ",");
            } else {
                return null;
            }
        }

        @Override
        public SearchScroll build() {
            return new SearchScroll(this);
        }
    }
}
