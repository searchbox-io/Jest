package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.searchbox.client.JestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author cihat keser
 */
public class SuggestResult extends JestResult {

    public SuggestResult(SuggestResult suggestResult) {
        super(suggestResult);
    }

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

        @Override
        public int hashCode() {
            return Objects.hash(
                    text,
                    offset,
                    length,
                    options);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }

            Suggestion rhs = (Suggestion) obj;
            return Objects.equals(text, rhs.text)
                    && Objects.equals(offset, rhs.offset)
                    && Objects.equals(length, rhs.length)
                    && Objects.equals(options, rhs.options);
        }
    }

}
