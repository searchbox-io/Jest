package io.searchbox.indices;

import com.google.gson.JsonObject;
import io.searchbox.AbstractAction;
import io.searchbox.Action;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class CreateIndex extends AbstractAction implements Action {

    private boolean isCreateOp = false;

    public CreateIndex(String indexName) {
        this.indexName = indexName;
        setURI(buildURI());
        setData(new JsonObject());
        isCreateOp = true;
    }

    public CreateIndex(String indexName, Map<String, String> settings) {
        this.indexName = indexName;
        setURI(buildURI());
        setData(settings);
    }

    @Override
    public String getRestMethodName() {
        return isCreateOp ? "PUT" : "POST";
    }
}
