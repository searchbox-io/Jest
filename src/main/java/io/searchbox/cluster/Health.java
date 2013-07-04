package io.searchbox.cluster;

import io.searchbox.AbstractAction;
import org.apache.commons.lang.StringUtils;

/**
 * @author Dogukan Sonmez
 * @author Neil Gentleman
 */
public class Health extends AbstractAction {

    @Override
    public String getURI() {
        StringBuilder sb = new StringBuilder("_cluster/health");
        String queryString = buildQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            sb.append(queryString);
        }
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

}
