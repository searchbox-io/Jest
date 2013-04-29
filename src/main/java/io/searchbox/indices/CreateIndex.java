package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

import java.util.Map;

import com.google.gson.JsonObject;

/**
 * @author Dogukan Sonmez
 */


public class CreateIndex extends AbstractAction implements Action {

    public CreateIndex(String indexName) {
        setURI(buildURI(indexName, null, null));
        setRestMethodName("PUT");
        setData(new JsonObject());
    }

    public CreateIndex(String indexName, Map<String, String> settings) {
        setURI(buildURI(indexName, null, null));
        setData(settings);
        setRestMethodName("POST");
    }
}
