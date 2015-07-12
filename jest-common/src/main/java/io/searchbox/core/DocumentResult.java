package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.searchbox.client.JestResult;

/**
 * @author Bartosz Polnik
 */
public class DocumentResult extends JestResult {
    public DocumentResult(Gson gson) {
        super(gson);
    }

    public String getIndex() {
        return getAsString(jsonObject.get("_index"));
    }

    public String getType() {
        return getAsString(jsonObject.get("_type"));
    }

    public String getId() {
        return getAsString(jsonObject.get("_id"));
    }


    private String getAsString(JsonElement jsonElement) {
        if(jsonElement == null) {
            return null;
        } else {
            return jsonElement.getAsString();
        }
    }
}
