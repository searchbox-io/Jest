package io.searchbox.core;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author Dogukan Sonmez
 */


public class Search extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Search.class.getName());

    public Search(String indexName, String type, String uriRequest) {
        setURI(buildURI(indexName, type, uriRequest));
        setRestMethodName("GET");
    }


    public Search(String indexName, String type, Object query) {
        setURI(buildURI(indexName, type));
        setRestMethodName("POST");
        setData(query);
    }

    protected String buildURI(String indexName, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(indexName)
                .append("/");
        if (StringUtils.isNotBlank(type)) {
            sb.append(type).append("/");
        }
        sb.append("_search");
        log.debug("Created URI for search action is : " + sb.toString());
        return sb.toString();
    }

    private String buildURI(String indexName, String type, String uriRequest) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

}
