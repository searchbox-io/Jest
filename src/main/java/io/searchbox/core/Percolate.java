package io.searchbox.core;

import org.apache.commons.lang.StringUtils;

/**
 * @author Dogukan Sonmez
 */


public class Percolate extends AbstractClientRequest implements ClientRequest {

    public Percolate(String indexName, String designedQueryName, Object query) {
        setURI(buildURI(indexName, designedQueryName));
        setData(query);
        setRestMethodName("PUT");
    }

    private String buildURI(String indexName, String designedQueryName) {
        StringBuilder sb = new StringBuilder();
        sb.append("_percolator")
                .append("/")
                .append(indexName)
                .append("/")
                .append(designedQueryName);
        return sb.toString();
    }

}
