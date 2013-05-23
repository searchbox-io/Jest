package io.searchbox.indices;

import com.google.gson.JsonObject;
import io.searchbox.AbstractAction;

/**
 * @author Dogukan Sonmez
 */


public class DeleteIndex extends AbstractAction {

    public DeleteIndex(String indexName) {
        this.indexName = indexName;
        setURI(buildURI());
    }

    public DeleteIndex(String indexName, String type) {
        this.indexName = indexName;
        this.typeName = type;
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    @Override
    public Boolean isOperationSucceed(JsonObject result) {
        return (result.get("ok").getAsBoolean() && result.get("acknowledged").getAsBoolean());
    }

}
