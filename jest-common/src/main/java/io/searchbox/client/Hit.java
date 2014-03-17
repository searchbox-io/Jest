package io.searchbox.client;

import com.google.gson.JsonElement;

/**
 * Immutable class representing a hit element in result.
 *
 * @param <T> type of source
 * @param <K> type of explanation
 *
 * @author cihat keser
 */
public class Hit<T,K> {
    public final T source;
    public final K explanation;

    public Hit(T source, K explanation) {
        this.source = source;
        this.explanation = explanation;
    }

}
