package io.searchbox.core;

import io.searchbox.Document;


/**
 * @author Dogukan Sonmez
 */


public class Delete extends AbstractAction implements Action {

    public Delete(Document document){
        setURI(buildURI(document));
        setRestMethodName("DELETE");
    }

    public Delete(String indexName){

    }
}
