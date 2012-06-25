package io.searchbox.core;

import io.searchbox.Document;
import org.apache.log4j.Logger;

/**
 * @author Dogukan Sonmez
 */


public class Percolate extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Percolate.class.getName());

    public Percolate(String indexName, String designedQueryName, Object query) {
        setURI(buildURI(indexName, designedQueryName));
        setData(query);
        setRestMethodName("PUT");
        setName("PERCOLATE");
    }

    public Percolate(Document document){
        if (!isValid(document)) throw new RuntimeException("Invalid document cannot be set for index");
        setURI(buildURI(document));
        setData(document.getSource());
        setRestMethodName("POST");
        setName("PERCOLATE");
    }

    private String buildURI(String indexName, String designedQueryName) {
        StringBuilder sb = new StringBuilder();
        sb.append("_percolator")
                .append("/")
                .append(indexName)
                .append("/")
                .append(designedQueryName);
        log.debug("Created URI for percolate request is : " + sb.toString());
        return sb.toString();
    }

    protected String buildURI(Document document){
        return null;
    }

}
