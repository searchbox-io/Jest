package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ferhat
 */
public class SearchScroll extends AbstractAction implements Action {

    final static Logger log = LoggerFactory.getLogger(SearchScroll.class);

    public SearchScroll(String scrollId, String scroll) {
        addParameter("scroll_id", scrollId);
        addParameter("scroll", scroll);
    }

    public String getURI() {
        StringBuilder sb = new StringBuilder();
        sb.append("/_search/scroll");
        String queryString = buildQueryString();
        if (StringUtils.isNotBlank(queryString)) sb.append(queryString);
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
}
