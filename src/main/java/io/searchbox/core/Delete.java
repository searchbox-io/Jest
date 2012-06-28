package io.searchbox.core;

import org.apache.log4j.Logger;

import java.util.List;


/**
 * @author Dogukan Sonmez
 */


public class Delete extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Delete.class.getName());

    public Delete(String indexName) {
        setURI(buildURI(indexName));
    }

    public Delete(String indexName, String typeName) {
        setURI(buildURI(indexName, typeName, null));
    }

    public Delete(String indexName, String typeName, String id) {
        setURI(buildURI(indexName, typeName, id));
    }

    public Delete(Doc doc) {
        setURI(buildURI(doc));
    }

    public Delete(List<Doc> docs) {
        setURI(null);
    }

    public Delete(String[] ids) {
        setURI(null);
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
