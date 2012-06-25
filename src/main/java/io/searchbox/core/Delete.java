package io.searchbox.core;

import io.searchbox.Document;
import org.apache.log4j.Logger;


/**
 * @author Dogukan Sonmez
 */


public class Delete extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Delete.class.getName());

    public Delete(Document document) {
        if(isValid(document)) {
            setURI(buildURI(document));
        }else {
            throw new RuntimeException("Invalid document cannot be set for index");
        }
    }

    public Delete(String indexName) {
        setURI(buildURI(indexName));
    }

    public String getName() {
        return "DELETE";
    }

    public String getRestMethodName() {
        return "DELETE";
    }

    private String buildURI(String indexName) {
        log.debug("Created uri: " + indexName);
        return indexName;
    }
}
