package io.searchbox.client;

import com.google.gson.*;
import io.searchbox.annotations.JestId;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */
public class JestResult {

    public static final String ES_METADATA_ID = "es_metadata_id";
    private static final Logger log = LoggerFactory.getLogger(JestResult.class);

    protected JsonObject jsonObject;
    protected String jsonString;
    protected String pathToResult;
    protected int responseCode;
    protected boolean isSucceeded;
    protected String errorMessage;
    protected Gson gson;

    private JestResult() {
    }

    public JestResult(JestResult source) {
        this.jsonObject = source.jsonObject;
        this.jsonString = source.jsonString;
        this.pathToResult = source.pathToResult;
        this.responseCode = source.responseCode;
        this.isSucceeded = source.isSucceeded;
        this.errorMessage = source.errorMessage;
        this.gson = source.gson;
    }

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

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
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
            errorMessage = jsonObject.get("error").toString();
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

    /**
     * @return null if operation did not succeed or the response is null or the "keys" field of the action is empty or
     * the response does not contain the key to source.
     * String representing the source JSON element(s) otherwise.
     * Elements are joined with a comma if there are multiple sources (e.g.: search with multiple hits).
     */
    public String getSourceAsString() {
        List<String> sources = getSourceAsStringList();
        return sources == null ? null : StringUtils.join(sources, ",");
    }

    /**
     * @return null if operation did not succeed or the response is null or the "keys" field of the action is empty or
     * the response does not contain the key to source.
     * List of strings representing the source JSON element(s) otherwise.
     */
    public List<String> getSourceAsStringList() {
        String[] keys = getKeys();
        if (!isSucceeded || jsonObject == null || keys == null || keys.length == 0 || !jsonObject.has(keys[0])) {
            return null;
        }

        List<String> sourceList = new ArrayList<String>();
        for (JsonElement element : extractSource(false)) {
            sourceList.add(element.toString());
        }
        return sourceList;
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
            for (JsonElement source : extractSource()) {
                T obj = createSourceObject(source, type);
                if (obj != null) {
                    objectList.add(obj);
                }
            }
        }

        return objectList;
    }

    protected List<JsonElement> extractSource() {
        return extractSource(true);
    }

    protected List<JsonElement> extractSource(boolean addEsMetadataIdField) {
        List<JsonElement> sourceList = new ArrayList<JsonElement>();

        if (jsonObject != null) {
            String[] keys = getKeys();
            if (keys == null) {
                sourceList.add(jsonObject);
            } else {
                String sourceKey = keys[keys.length - 1];
                JsonElement obj = jsonObject.get(keys[0]);
                if (keys.length > 1) {
                    for (int i = 1; i < keys.length - 1; i++) {
                        obj = ((JsonObject) obj).get(keys[i]);
                    }

                    if (obj.isJsonObject()) {
                        JsonElement source = obj.getAsJsonObject().get(sourceKey);
                        if (source != null) {
                            sourceList.add(source);
                        }
                    } else if (obj.isJsonArray()) {
                        for (JsonElement element : obj.getAsJsonArray()) {
                            if (element instanceof JsonObject) {
                                JsonObject currentObj = element.getAsJsonObject();
                                JsonObject source = currentObj.getAsJsonObject(sourceKey);
                                if (source != null) {
                                    JsonObject copy = GsonUtils.deepCopy(source);
                                    if (addEsMetadataIdField) {
                                        copy.add(ES_METADATA_ID, currentObj.get("_id"));
                                    }
                                    sourceList.add(copy);
                                }
                            }
                        }
                    }
                } else if (obj != null) {
                    JsonElement copy = GsonUtils.deepCopy(obj);
                    JsonElement objId = jsonObject.get("_id");
                    if ((objId != null) && copy.isJsonObject() && addEsMetadataIdField) {
                        copy.getAsJsonObject().add(ES_METADATA_ID, objId);
                    }
                    sourceList.add(copy);
                }
            }
        }

        return sourceList;
    }

    protected <T> T createSourceObject(JsonElement source, Class<T> type) {
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
        return pathToResult == null ? null : pathToResult.split("/");
    }

}
