package io.searchbox.core;

import io.searchbox.Document;
import org.apache.log4j.Logger;


/**
 * @author Dogukan Sonmez
 */


public class Delete extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Delete.class.getName());

    public Delete(Document document) {
        setURI(buildURI(document));
        setRestMethodName("DELETE");
    }

    public Delete(String indexName) {
        setURI(buildURI(indexName));
        setRestMethodName("DELETE");
    }

    private String buildURI(String indexName) {
        log.debug("Created uri: " + indexName);
        return indexName;
    }
}
