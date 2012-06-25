package io.searchbox.core;

import io.searchbox.Document;


/**
 * @author Dogukan Sonmez
 */


public class Get extends AbstractAction implements Action {

    public Get(Document document) {
        if (!isValidWithId(document)) throw new RuntimeException("Invalid document cannot be set for index");
        setURI(buildURI(document));
        setRestMethodName("GET");
        setName("GET");
    }

}
