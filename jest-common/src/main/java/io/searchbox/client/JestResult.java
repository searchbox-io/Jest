package io.searchbox.client;

import com.google.gson.*;
import io.searchbox.annotations.JestId;
import io.searchbox.core.search.facet.Facet;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Dogukan Sonmez
 */

public class JestResult {

    public static final String ES_METADATA_ID = "es_metadata_id";
    public static final String EXPLANATION_KEY = "_explanation";
    final static Logger log = LoggerFactory.getLogger(JestResult.class);

    private JsonObject jsonObject;
    private String jsonString;
    private String pathToResult;
    private boolean isSucceeded;
    private String errorMessage;
    private Gson gson;

    public JestResult(Gson gson) {
        this.gson = gson;
    }

    public String getPathToResult() {
        return pathToResult;
    }

    public void setPathToResult(String pathToResult) {
        this.pathToResult = pathToResult;
    }

    public Object getValue(String key) {
        return getJsonMap().get(key);
    }

    public boolean isSucceeded() {
        return isSucceeded;
    }

    public void setSucceeded(boolean succeeded) {
        isSucceeded = succeeded;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * manually set an error message, eg. for the cases where non-200 response code is received
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        if (jsonObject.get("error") != null) {
            errorMessage = jsonObject.get("error").getAsString();
        }
    }

    @Deprecated
    @SuppressWarnings("rawtypes")
    public Map getJsonMap() {
        return gson.fromJson(jsonObject, Map.class);
    }

    public void setJsonMap(Map<String, Object> resultMap) {
        String json = gson.toJson(resultMap, Map.class);
        setJsonObject(new JsonParser().parse(json).getAsJsonObject());
    }

    public <T> T getSourceAsObject(Class<T> clazz) {
        T sourceAsObject = null;

        List<T> sources = getSourceAsObjectList(clazz);
        if (sources.size() > 0) {
            sourceAsObject = sources.get(0);
        }

        return sourceAsObject;
    }

    public <T> List<T> getSourceAsObjectList(Class<T> type) {
        List<T> objectList = new ArrayList<T>();

        if (isSucceeded) {
            for (Pair<JsonElement,JsonElement> pair : extractSource()) {
                T obj = createSourceObject(pair.getLeft(), type);
                if (obj != null) {
                    objectList.add(obj);
                }
            }
        }

        return objectList;
    }

    public <T, U> Pair<T, U> getSourceAsObject(Class<T> clazz, Class<U> explanationClazz) {
        Pair<T, U> sourceWithExplanation = null;

        List<Pair<T, U>> sources = getSourceAsObjectList(clazz, explanationClazz);
        if (sources.size() > 0) {
            sourceWithExplanation = sources.get(0);
        }

        return sourceWithExplanation;
    }

    public <T, U> List<Pair<T, U>> getSourceAsObjectList(Class<T> type, Class<U> explanationType) {
        List<Pair<T, U>> objectList = new ArrayList<Pair<T, U>>();

        if (isSucceeded) {
            for (Pair<JsonElement, JsonElement> sourcePair : extractSource()) {
                T obj = createSourceObject(sourcePair.getLeft(), type);
                if (obj != null) {
                    U explanation = createSourceObject(sourcePair.getRight(), explanationType);
                    objectList.add(new ImmutablePair<T, U>(obj, explanation));
                }
            }
        }

        return objectList;
    }

    /**
     *
     * @return list of ImmutablePair instances with source as left and explanation as right value
     */
    protected List<Pair<JsonElement, JsonElement>> extractSource() {
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

    private <T> T createSourceObject(JsonElement source, Class<T> type) {
        T obj = null;
        try {

            String json = source.toString();
            obj = gson.fromJson(json, type);

            // Check if JestId is visible
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(JestId.class)) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        if (value == null) {
                            Class<?> fieldType = field.getType();
                            JsonElement id = ((JsonObject) source).get(ES_METADATA_ID);
                            field.set(obj, getAs(id, fieldType));
                        }
                    } catch (IllegalAccessException e) {
                        log.error("Unhandled exception occurred while getting annotated id from source");
                    }
                    break;
                }
            }

        } catch (Exception e) {
            log.error("Unhandled exception occurred while converting source to the object ." + type.getCanonicalName(), e);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private <T> T getAs(JsonElement id, Class<T> fieldType) throws IllegalAccessException {
        if (id.isJsonNull()) {
            return null;
        }
        if (fieldType.isAssignableFrom(String.class)) {
            return (T) id.getAsString();
        }
        if (fieldType.isAssignableFrom(Number.class)) {
            return (T) id.getAsNumber();
        }
        if (fieldType.isAssignableFrom(BigDecimal.class)) {
            return (T) id.getAsBigDecimal();
        }
        if (fieldType.isAssignableFrom(Double.class)) {
            Object o = id.getAsDouble();
            return (T) o;
        }
        if (fieldType.isAssignableFrom(Float.class)) {
            Object o = id.getAsFloat();
            return (T) o;
        }
        if (fieldType.isAssignableFrom(BigInteger.class)) {
            return (T) id.getAsBigInteger();
        }
        if (fieldType.isAssignableFrom(Long.class)) {
            Object o = id.getAsLong();
            return (T) o;
        }
        if (fieldType.isAssignableFrom(Integer.class)) {
            Object o = id.getAsInt();
            return (T) o;
        }
        if (fieldType.isAssignableFrom(Short.class)) {
            Object o = id.getAsShort();
            return (T) o;
        }
        if (fieldType.isAssignableFrom(Character.class)) {
            return (T) (Character) id.getAsCharacter();
        }
        if (fieldType.isAssignableFrom(Byte.class)) {
            return (T) (Byte) id.getAsByte();
        }
        if (fieldType.isAssignableFrom(Boolean.class)) {
            return (T) (Boolean) id.getAsBoolean();
        }

        throw new RuntimeException("cannot assign " + id + " to " + fieldType);
    }

    protected String[] getKeys() {
        return pathToResult == null ? null : (pathToResult + "").split("/");
    }

    public <T extends Facet> List<T> getFacets(Class<T> type) {
        List<T> facets = new ArrayList<T>();
        if (jsonObject != null) {
            Constructor<T> c;
            try {
                JsonObject facetsMap = (JsonObject) jsonObject.get("facets");
                if (facetsMap == null)
                    return facets;
                for (Entry<String, JsonElement> facetEntry : facetsMap.entrySet()) {
                    JsonObject facet = facetEntry.getValue().getAsJsonObject();
                    if (facet.get("_type").getAsString().equalsIgnoreCase(type.getField("TYPE").get(null).toString())) {
                        // c = (Constructor<T>)
                        // Class.forName(type.getName()).getConstructor(String.class,JsonObject.class);

                        c = type.getConstructor(String.class, JsonObject.class);
                        facets.add((T) c.newInstance(facetEntry.getKey(), facetEntry.getValue()));

                    }
                }
                return facets;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return facets;
    }
}
