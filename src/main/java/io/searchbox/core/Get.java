package io.searchbox.core;

import java.util.List;


/**
 * @author Dogukan Sonmez
 */


public class Get extends AbstractAction implements Action {

    public Get(String indexName, String typeName, String id) {
        if (!isValid(indexName, typeName, id)) throw new RuntimeException("Invalid document cannot be set for index");
        setURI(buildURI(indexName, typeName, id));
    }

    public Get(Doc doc) {
        if (!isValid(doc)) throw new RuntimeException("Invalid document cannot be set for index");
        setURI(buildURI(doc));
    }

    public Get(List<Doc> docs) {
        if (!isValid(docs)) throw new RuntimeException("Invalid document cannot be set for index");
        setURI(null);
    }

    public Get(String type, String id) {
        setURI(null);
    }

    public Get(String type, String[] ids) {
        setURI(null);
    }

    public Get(String[] ids) {
        setURI(null);
    }

    @Override
    public String getName() {
        return "GET";
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }
}
