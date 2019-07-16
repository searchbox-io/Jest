package io.searchbox.client;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.searchbox.annotations.JestId;
import io.searchbox.annotations.JestVersion;
import io.searchbox.cloning.CloneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
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
    public static final String ES_METADATA_VERSION = "es_metadata_version";
    private static final Logger log = LoggerFactory.getLogger(JestResult.class);

    protected static class MetaField {
        public final String internalFieldName;
        public final String esFieldName;
        public final Class<? extends Annotation> annotationClass;

        MetaField(String internalFieldName, String esFieldName, Class<? extends Annotation> annotationClass) {
            this.internalFieldName = internalFieldName;
            this.esFieldName = esFieldName;
            this.annotationClass = annotationClass;
        }
    }

    protected static final ImmutableList<MetaField> META_FIELDS = ImmutableList.of(
            new MetaField(ES_METADATA_ID, "_id", JestId.class),
            new MetaField(ES_METADATA_VERSION, "_version", JestVersion.class)
    );

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
        return sources == null ? null : Joiner.on(',').join(sources);
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
        return getSourceAsObject(clazz, true);
    }

    public <T> T getSourceAsObject(Class<T> clazz, boolean addEsMetadataFields) {
        T sourceAsObject = null;

        List<T> sources = getSourceAsObjectList(clazz, addEsMetadataFields);
        if (sources.size() > 0) {
            sourceAsObject = sources.get(0);
        }

        return sourceAsObject;
    }

    public <T> List<T> getSourceAsObjectList(Class<T> type) {
        return getSourceAsObjectList(type, true);
    }

    public <T> List<T> getSourceAsObjectList(Class<T> type, boolean addEsMetadataFields) {
        List<T> objectList = new ArrayList<T>();

        if (isSucceeded) {
            for (JsonElement source : extractSource(addEsMetadataFields)) {
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

    protected List<JsonElement> extractSource(boolean addEsMetadataFields) {
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
                                    JsonObject copy = (JsonObject) CloneUtils.deepClone(source);
                                    if (addEsMetadataFields) {
                                        for (MetaField metaField : META_FIELDS) {
                                            copy.add(metaField.internalFieldName, currentObj.get(metaField.esFieldName));
                                        }
                                    }
                                    sourceList.add(copy);
                                }
                            }
                        }
                    }
                } else if (obj != null) {
                    JsonElement copy = CloneUtils.deepClone(obj);
                    if (addEsMetadataFields && copy.isJsonObject()) {
                        JsonObject copyObject = copy.getAsJsonObject();
                        for (MetaField metaField : META_FIELDS) {
                            JsonElement metaElement = jsonObject.get(metaField.esFieldName);
                            if (metaElement != null) {
                                copyObject.add(metaField.internalFieldName, metaElement);
                            }
                        }
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

            obj = gson.fromJson(source, type);

            // Check if JestId is visible
            Class clazz = type;
            int knownMetadataFieldsCount = META_FIELDS.size();
            int foundFieldsCount = 0;
            boolean allFieldsFound = false;
            while (clazz != null && !allFieldsFound) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (foundFieldsCount == knownMetadataFieldsCount) {
                        allFieldsFound = true;
                        break;
                    }
                    for (MetaField metaField : META_FIELDS) {
                        if (field.isAnnotationPresent(metaField.annotationClass) && setAnnotatedField(obj, source, field, metaField.internalFieldName)) {
                            foundFieldsCount++;
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }

        } catch (Exception e) {
            log.error("Unhandled exception occurred while converting source to the object ." + type.getCanonicalName(), e);
        }
        return obj;
    }

    private <T> boolean setAnnotatedField(T obj, JsonElement source, Field field, String fieldName) {
        try {
            field.setAccessible(true);
            Object value = field.get(obj);
            if (value == null) {
                Class<?> fieldType = field.getType();
                JsonElement element = ((JsonObject) source).get(fieldName);
                field.set(obj, getAs(element, fieldType));
                return true;
            }
        } catch (IllegalAccessException e) {
            log.error("Unhandled exception occurred while setting annotated field from source");
        }
        return false;
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

    @Override
    public String toString() {
        return "Result: "             + getJsonString()
                + ", isSucceeded: "   + isSucceeded()
                + ", response code: " + getResponseCode()
                + ", error message: " + getErrorMessage();
    }
}
