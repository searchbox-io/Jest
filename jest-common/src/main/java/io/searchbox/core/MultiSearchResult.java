package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
        public final SearchResult searchResult;

        public MultiSearchResponse(JsonObject jsonObject) {
            if(jsonObject.has(ERROR_KEY)) {
                isError = true;
                errorMessage = jsonObject.get(ERROR_KEY).toString();
                searchResult = null;
            } else {
                isError = false;
                errorMessage = null;

                searchResult = new SearchResult(gson);
                searchResult.setSucceeded(true);
                searchResult.setResponseCode(responseCode);
                searchResult.setJsonObject(jsonObject);
                searchResult.setJsonString(jsonObject.toString());
                searchResult.setPathToResult("hits/hits/_source");
            }
        }
    }
}
