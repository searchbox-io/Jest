package io.searchbox.core;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.params.Parameters;
import org.apache.commons.lang3.StringUtils;

/**
 * @author ferhat
 */
public class SearchScroll extends GenericResultAbstractAction {
    @VisibleForTesting
    static final int MAX_SCROLL_ID_LENGTH = 1900;
    private final String scrollId;

    public SearchScroll(Builder builder) {
        super(builder);
        this.scrollId = builder.getScrollId();
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
        return scrollId.length() > MAX_SCROLL_ID_LENGTH ? "POST" : "GET";
    }

    @Override
    public Object getData(Gson gson) {
        return scrollId.length() > MAX_SCROLL_ID_LENGTH ? scrollId : super.getData(gson);
    }

    @Override
    public String getPathToResult() {
        return "hits/hits/_source";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<SearchScroll, Builder> {

        private final String scrollId;

        public Builder(String scrollId, String scroll) {
            this.scrollId = scrollId;
            if (scrollId.length() <= MAX_SCROLL_ID_LENGTH) {
                setParameter(Parameters.SCROLL_ID, scrollId);
            }
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

        public String getScrollId() {
            return scrollId;
        }
    }
}
