package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.cloning.CloneUtils;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.RootAggregation;

import java.util.*;

/**
 * @author cihat keser
 */
public class SearchResult extends JestResult {

    public static final String EXPLANATION_KEY = "_explanation";
    public static final String HIGHLIGHT_KEY = "highlight";
    public static final String SORT_KEY = "sort";
    public static final String[] PATH_TO_TOTAL = "hits/total".split("/");
    public static final String[] PATH_TO_MAX_SCORE = "hits/max_score".split("/");

    public SearchResult(SearchResult searchResult) {
        super(searchResult);
    }

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

    public <T> Hit<T, Void> getFirstHit(Class<T> sourceType) {
        return getFirstHit(sourceType, Void.class);
    }

    public <T, K> Hit<T, K> getFirstHit(Class<T> sourceType, Class<K> explanationType) {
        Hit<T, K> hit = null;

        List<Hit<T, K>> hits = getHits(sourceType, explanationType, true);
        if (!hits.isEmpty()) {
            hit = hits.get(0);
        }

        return hit;
    }

    public <T> List<Hit<T, Void>> getHits(Class<T> sourceType) {
        return getHits(sourceType, true);
    }

    public <T> List<Hit<T, Void>> getHits(Class<T> sourceType, boolean addEsMetadataFields) {
        return getHits(sourceType, Void.class, addEsMetadataFields);
    }

    public <T, K> List<Hit<T, K>> getHits(Class<T> sourceType, Class<K> explanationType) {
        return getHits(sourceType, explanationType, false, true);
    }

    public <T, K> List<Hit<T, K>> getHits(Class<T> sourceType, Class<K> explanationType, boolean addEsMetadataFields) {
        return getHits(sourceType, explanationType, false, addEsMetadataFields);
    }

    protected <T, K> List<Hit<T, K>> getHits(Class<T> sourceType, Class<K> explanationType, boolean returnSingle, boolean addEsMetadataFields) {
        List<Hit<T, K>> sourceList = new ArrayList<Hit<T, K>>();

        if (jsonObject != null) {
            String[] keys = getKeys();
            if (keys != null) { // keys would never be null in a standard search scenario (i.e.: unless search class is overwritten)
                String sourceKey = keys[keys.length - 1];
                JsonElement obj = jsonObject.get(keys[0]);
                for (int i = 1; i < keys.length - 1; i++) {
                    obj = ((JsonObject) obj).get(keys[i]);
                }

                if (obj.isJsonObject()) {
                    sourceList.add(extractHit(sourceType, explanationType, obj, sourceKey, addEsMetadataFields));
                } else if (obj.isJsonArray()) {
                    for (JsonElement hitElement : obj.getAsJsonArray()) {
                        sourceList.add(extractHit(sourceType, explanationType, hitElement, sourceKey, addEsMetadataFields));
                        if (returnSingle) break;
                    }
                }
            }
        }

        return sourceList;
    }

    protected <T, K> Hit<T, K> extractHit(Class<T> sourceType, Class<K> explanationType, JsonElement hitElement, String sourceKey, boolean addEsMetadataFields) {
        Hit<T, K> hit = null;

        if (hitElement.isJsonObject()) {
            JsonObject hitObject = hitElement.getAsJsonObject();
            JsonObject source = hitObject.getAsJsonObject(sourceKey);


            String index = hitObject.get("_index").getAsString();
            String type = hitObject.get("_type").getAsString();

            String id = hitObject.get("_id").getAsString();

            Double score = null;
            if (hitObject.has("_score") && !hitObject.get("_score").isJsonNull()) {
                score = hitObject.get("_score").getAsDouble();
            }

            String parent = null;
            String routing = null;

            if (hitObject.has("_parent") && !hitObject.get("_parent").isJsonNull()) {
                parent = hitObject.get("_parent").getAsString();
            }

            if (hitObject.has("_routing") && !hitObject.get("_routing").isJsonNull()) {
                routing = hitObject.get("_routing").getAsString();
            }

            JsonElement explanation = hitObject.get(EXPLANATION_KEY);
            Map<String, List<String>> highlight = extractHighlight(hitObject.getAsJsonObject(HIGHLIGHT_KEY));
            List<String> sort = extractSort(hitObject.getAsJsonArray(SORT_KEY));

            List<String> matchedQueries = new ArrayList<>();
            if (hitObject.has("matched_queries") && !hitObject.get("matched_queries").isJsonNull()) {
                JsonArray rawMatchedQueries = hitObject.get("matched_queries").getAsJsonArray();
                rawMatchedQueries.forEach(matchedQuery -> {
                    matchedQueries.add(matchedQuery.getAsString());
                });
            }

            if (addEsMetadataFields) {
                JsonObject clonedSource = null;
                for (MetaField metaField : META_FIELDS) {
                    JsonElement metaElement = hitObject.get(metaField.esFieldName);
                    if (metaElement != null) {
                        if (clonedSource == null) {
                            if (source == null) {
                                clonedSource = new JsonObject();
                            } else {
                                clonedSource = (JsonObject) CloneUtils.deepClone(source);
                            }
                        }
                        clonedSource.add(metaField.internalFieldName, metaElement);
                    }
                }
                if (clonedSource != null) {
                    source = clonedSource;
                }
            }

            hit = new Hit<T, K>(
                    sourceType,
                    source,
                    explanationType,
                    explanation,
                    highlight,
                    sort,
                    index,
                    type,
                    id,
                    score,
                    parent,
                    routing,
                    matchedQueries
            );

        }

        return hit;
    }

    protected List<String> extractSort(JsonArray sort) {
        if (sort == null) {
            return null;
        }

        List<String> retval = new ArrayList<String>(sort.size());
        for (JsonElement sortValue : sort) {
            retval.add(sortValue.isJsonNull() ? "" : sortValue.getAsString());
        }
        return retval;
    }

    protected Map<String, List<String>> extractHighlight(JsonObject highlight) {
        Map<String, List<String>> retval = null;

        if (highlight != null) {
            Set<Map.Entry<String, JsonElement>> highlightSet = highlight.entrySet();
            retval = new HashMap<String, List<String>>(highlightSet.size());

            for (Map.Entry<String, JsonElement> entry : highlightSet) {
                List<String> fragments = new ArrayList<String>();
                for (JsonElement element : entry.getValue().getAsJsonArray()) {
                    fragments.add(element.getAsString());
                }
                retval.put(entry.getKey(), fragments);
            }
        }

        return retval;
    }

    public Long getTotal() {
        Long total = null;
        JsonElement obj = getPath(PATH_TO_TOTAL);
        if (obj != null) total = obj.getAsLong();
        return total;
    }

    public Float getMaxScore() {
        Float maxScore = null;
        JsonElement obj = getPath(PATH_TO_MAX_SCORE);
        if (obj != null && !obj.isJsonNull()) maxScore = obj.getAsFloat();
        return maxScore;
    }

    protected JsonElement getPath(String[] path) {
        JsonElement retval = null;
        if (jsonObject != null) {
            JsonElement obj = jsonObject;
            for (String component : path) {
                if (obj == null) break;
                obj = ((JsonObject) obj).get(component);
            }
            retval = obj;
        }
        return retval;
    }

    public MetricAggregation getAggregations() {
        final String rootAggrgationName = "aggs";
        if (jsonObject == null) return new RootAggregation(rootAggrgationName, new JsonObject());
        if (jsonObject.has("aggregations"))
            return new RootAggregation(rootAggrgationName, jsonObject.getAsJsonObject("aggregations"));
        if (jsonObject.has("aggs")) return new RootAggregation(rootAggrgationName, jsonObject.getAsJsonObject("aggs"));

        return new RootAggregation(rootAggrgationName, new JsonObject());
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
        public final Map<String, List<String>> highlight;
        public final List<String> sort;
        public final String index;
        public final String type;
        public final String id;
        public final Double score;
        public final String parent;
        public final String routing;
        public final List<String> matchedQueries;

        public Hit(Class<T> sourceType, JsonElement source, Class<K> explanationType, JsonElement explanation) {
            this(sourceType, source, explanationType, explanation, null, null);
        }

        public Hit(Class<T> sourceType, JsonElement source, Class<K> explanationType, JsonElement explanation,
                   Map<String, List<String>> highlight, List<String> sort) {
            this(sourceType, source, explanationType, explanation, highlight, sort, null, null, null, null, null, null, null);
        }

        public Hit(Class<T> sourceType, JsonElement source, Class<K> explanationType, JsonElement explanation,
                   Map<String, List<String>> highlight, List<String> sort, String index, String type, String id,
                   Double score, String parent, String routing, List<String> matchedQueries) {
            if (source == null) {
                this.source = null;
            } else {
                this.source = createSourceObject(source, sourceType);
            }
            if (explanation == null) {
                this.explanation = null;
            } else {
                this.explanation = createSourceObject(explanation, explanationType);
            }
            this.highlight = highlight;
            this.sort = sort;

            this.index = index;
            this.type = type;
            this.id = id;
            this.score = score;
            this.parent = parent;
            this.routing = routing;
            this.matchedQueries = matchedQueries;
        }
        
        public Hit(Class<T> sourceType, JsonElement source, Class<K> explanationType, JsonElement explanation,
                Map<String, List<String>> highlight, List<String> sort, String index, String type, String id, Double score) {
            this(sourceType, source, explanationType, explanation, highlight, sort, index, type, id, score, null, null, null);
        }

        public Hit(T source) {
            this(source, null, null, null);
        }

        public Hit(T source, K explanation) {
            this(source, explanation, null, null);
        }

        public Hit(T source, K explanation, Map<String, List<String>> highlight, List<String> sort) {
            this(source, explanation, highlight, sort, null, null, null, null, null);
        }

        public Hit(T source, K explanation, Map<String, List<String>> highlight, List<String> sort, String index, String type, String id, Double score, List<String> matchedQueries) {
            this.source = source;
            this.explanation = explanation;
            this.highlight = highlight;
            this.sort = sort;
            this.index = index;
            this.type = type;
            this.id = id;
            this.score = score;
            this.parent = null;
            this.routing = null;
            this.matchedQueries = matchedQueries;
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    source,
                    explanation,
                    highlight,
                    sort,
                    index,
                    type,
                    id);
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

            Hit rhs = (Hit) obj;
            return Objects.equals(source, rhs.source)
                    && Objects.equals(explanation, rhs.explanation)
                    && Objects.equals(highlight, rhs.highlight)
                    && Objects.equals(sort, rhs.sort)
                    && Objects.equals(index, rhs.index)
                    && Objects.equals(type, rhs.type)
                    && Objects.equals(id, rhs.id);
        }
    }

}
