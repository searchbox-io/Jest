package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cihat keser
 */
public class SuggestResult extends JestResult {

    public SuggestResult(Gson gson) {
        super(gson);
    }

    public List<Suggestion> getSuggestions(String suggestionName) {
        List<Suggestion> suggestions = new ArrayList<Suggestion>();

        if (jsonObject != null && jsonObject.has(suggestionName)) {
            for (JsonElement suggestionElement : jsonObject.getAsJsonArray(suggestionName)) {
                suggestions.add(gson.fromJson(suggestionElement, Suggestion.class));
            }
        }

        return suggestions;
    }

    public class Suggestion {
        public final String text;
        public final Integer offset;
        public final Integer length;
        public final List<Map<String, Object>> options;

        public Suggestion(String text, Integer offset, Integer length, List<Map<String, Object>> options) {
            this.text = text;
            this.offset = offset;
            this.length = length;
            this.options = options;
        }
    }

}
