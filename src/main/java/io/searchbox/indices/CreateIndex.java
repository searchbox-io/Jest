package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class CreateIndex extends AbstractAction implements Action {

    public CreateIndex(String indexName) {
        setURI(buildURI(indexName, null, null));
        setRestMethodName("PUT");
        setData(new HashMap());
    }

    public CreateIndex(String indexName, Map<String, String> settings) {
        setURI(buildURI(indexName, null, null));
        setData(settings);
        setRestMethodName("POST");
    }
}
