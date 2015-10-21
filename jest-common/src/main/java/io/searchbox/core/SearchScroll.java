package io.searchbox.core;

import com.google.common.annotations.VisibleForTesting;
import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.params.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author ferhat
 */
public class SearchScroll extends GenericResultAbstractAction {
    @VisibleForTesting
    static final int MAX_SCROLL_ID_LENGTH = 1900;
    private final String restMethodName;

    protected SearchScroll(Builder builder) {
        super(builder);

        if(builder.getScrollId().length() > MAX_SCROLL_ID_LENGTH) {
            this.restMethodName = "POST";
            this.payload = builder.getScrollId();
        } else {
            this.restMethodName = "GET";
        }
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_search/scroll";
    }

    @Override
    public String getRestMethodName() {
        return this.restMethodName;
    }

    @Override
    public String getPathToResult() {
        return "hits/hits/_source";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .isEquals();
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
