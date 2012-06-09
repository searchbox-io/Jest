package io.searchbox.core;


import org.apache.commons.lang.StringUtils;

/**
 * @author Dogukan Sonmez
 */


public class Search extends AbstractAction implements Action {

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
        return sb.toString();
    }

    private String buildURI(String indexName, String type, String uriRequest) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

}
