package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cihat keser
 */
public class MultiSearchResult extends JestResult {
    private static final String RESPONSES_KEY = "responses";
    private static final String ERROR_KEY = "error";

    public MultiSearchResult(MultiSearchResult source) {
        super(source);
    }

    public MultiSearchResult(Gson gson) {
        super(gson);
    }

    public List<MultiSearchResponse> getResponses() {
        List<MultiSearchResponse> multiSearchResponses = new ArrayList<MultiSearchResponse>();

        if(jsonObject != null && jsonObject.has(RESPONSES_KEY)) {
            JsonArray responsesArray = jsonObject.getAsJsonArray(RESPONSES_KEY);
            for(JsonElement responseElement : responsesArray) {
                multiSearchResponses.add(new MultiSearchResponse(responseElement.getAsJsonObject()));
            }
        }

        return multiSearchResponses;
    }

    public class MultiSearchResponse {

        public final boolean isError;
        public final String errorMessage;
        public final JsonElement error;
        public final SearchResult searchResult;

        public MultiSearchResponse(JsonObject jsonObject) {
            final JsonElement error = jsonObject.get(ERROR_KEY);
            if(error != null) {
                this.isError = true;
                this.error = error;
                if (error.isJsonPrimitive()) {
                    this.errorMessage = error.getAsString();
                } else if (error.isJsonObject()){
                    this.errorMessage = error.getAsJsonObject().get("reason").getAsString();
                } else {
                    this.errorMessage = error.toString();
                }
                this.searchResult = null;
            } else {
                this.isError = false;
                this.errorMessage = null;
                this.error = JsonNull.INSTANCE;

                this.searchResult = new SearchResult(gson);
                this.searchResult.setSucceeded(true);
                this.searchResult.setResponseCode(responseCode);
                this.searchResult.setJsonObject(jsonObject);
                this.searchResult.setJsonString(jsonObject.toString());
                this.searchResult.setPathToResult("hits/hits/_source");
            }
        }
    }
}
