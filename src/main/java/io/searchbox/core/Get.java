package io.searchbox.core;

import io.searchbox.Document;


/**
 * @author Dogukan Sonmez
 */


public class Get extends AbstractAction implements Action {

    public Get(Document document) {
        setURI(buildURI(document));
        setRestMethodName("GET");
        setName("GET");
    }

}
