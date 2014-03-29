package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cihat keser
 */
public class SearchResult extends JestResult {

    public static final String EXPLANATION_KEY = "_explanation";

    public SearchResult(Gson gson) {
        super(gson);
    }

    @Override
    @Deprecated
    public <T> T getSourceAsObject(Class<T> clazz) {
        return super.getSourceAsObject(clazz);
    }

    @Override
    @Deprecated
    public <T> List<T> getSourceAsObjectList(Class<T> type) {
        return super.getSourceAsObjectList(type);
    }

    public <T, K> List<Hit<T, K>> getHits(Class<T> sourceType, Class<K> explanationType) {
        List<Hit<T, K>> hits = new ArrayList<Hit<T, K>>();

        if (isSucceeded) {
            for (Pair<JsonElement, JsonElement> sourcePair : extractSearchSource(false)) {
                T source = createSourceObject(sourcePair.getLeft(), sourceType);
                if (source != null) {
                    K explanation = createSourceObject(sourcePair.getRight(), explanationType);
                    hits.add(new Hit<T, K>(source, explanation));
                }
            }
        }

        return hits;
    }

    public <T> List<Hit<T, Void>> getHits(Class<T> sourceType) {
        List<Hit<T, Void>> hits = new ArrayList<Hit<T, Void>>();

        if (isSucceeded) {
            for (Pair<JsonElement, JsonElement> sourcePair : extractSearchSource(false)) {
                T source = createSourceObject(sourcePair.getLeft(), sourceType);
                if (source != null) {
                    hits.add(new Hit<T, Void>(source, null));
                }
            }
        }

        return hits;
    }

    public <T> Hit<T, Void> getFirstHit(Class<T> sourceType) {
        Hit<T, Void> hit = null;

        if (isSucceeded) {
            for (Pair<JsonElement, JsonElement> sourcePair : extractSearchSource(true)) {
                T source = createSourceObject(sourcePair.getLeft(), sourceType);
                if (source != null) {
                    hit = new Hit<T, Void>(source, null);
                }
            }
        }

        return hit;
    }

    public <T, K> Hit<T, K> getFirstHit(Class<T> sourceType, Class<K> explanationType) {
        Hit<T, K> hit = null;

        if (isSucceeded) {
            for (Pair<JsonElement, JsonElement> sourcePair : extractSearchSource(true)) {
                T source = createSourceObject(sourcePair.getLeft(), sourceType);
                if (source != null) {
                    K explanation = createSourceObject(sourcePair.getRight(), explanationType);
                    hit = new Hit<T, K>(source, explanation);
                }
            }
        }

        return hit;
    }

    protected List<Pair<JsonElement, JsonElement>> extractSearchSource(boolean returnFirst) {
        List<Pair<JsonElement, JsonElement>> sourceList = new ArrayList<Pair<JsonElement, JsonElement>>();

        if (jsonObject != null) {
            String[] keys = getKeys();
            if (keys == null) {
                sourceList.add(new ImmutablePair<JsonElement, JsonElement>(jsonObject, null));
            } else {
                String sourceKey = keys[keys.length - 1];
                JsonElement obj = jsonObject.get(keys[0]);
                if (keys.length > 1) {
                    for (int i = 1; i < keys.length - 1; i++) {
                        obj = ((JsonObject) obj).get(keys[i]);
                    }

                    if (obj.isJsonObject()) {
                        JsonElement source = ((JsonObject) obj).get(sourceKey);
                        JsonElement explanation = ((JsonObject) obj).get(EXPLANATION_KEY);
                        if (source != null) {
                            sourceList.add(new ImmutablePair<JsonElement, JsonElement>(source, explanation));
                        }
                    } else if (obj.isJsonArray()) {
                        for (JsonElement newObj : (JsonArray) obj) {
                            if (newObj instanceof JsonObject) {
                                JsonObject source = (JsonObject) ((JsonObject) newObj).get(sourceKey);
                                JsonElement explanation = (JsonObject) ((JsonObject) newObj).get(EXPLANATION_KEY);
                                if (source != null) {
                                    source.add(ES_METADATA_ID, ((JsonObject) newObj).get("_id"));
                                    sourceList.add(new ImmutablePair<JsonElement, JsonElement>(source, explanation));
                                    if (returnFirst) break;
                                }
                            }
                        }
                    }
                } else if (obj != null) {
                    JsonElement explanation = jsonObject.get(EXPLANATION_KEY);
                    sourceList.add(new ImmutablePair<JsonElement, JsonElement>(obj, explanation));
                }
            }
        }

        return sourceList;
    }

    /**
     * Immutable class representing a search hit.
     *
     * @param <T> type of source
     * @param <K> type of explanation
     * @author cihat keser
     */
    public class Hit<T, K> {
        public final T source;
        public final K explanation;

        public Hit(T source, K explanation) {
            this.source = source;
            this.explanation = explanation;
        }
    }

}
