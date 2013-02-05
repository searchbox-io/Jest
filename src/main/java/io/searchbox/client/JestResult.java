package io.searchbox.client;

import com.google.gson.Gson;
import io.searchbox.annotations.JestId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class JestResult {

    final static Logger log = LoggerFactory.getLogger(JestResult.class);
    public static final String ES_METADATA_ID = "es_metadata_id";

    private Map jsonMap;

    private String jsonString;

    private String pathToResult;

    private boolean isSucceeded;

    public String getPathToResult() {
        return pathToResult;
    }

    public void setPathToResult(String pathToResult) {
        this.pathToResult = pathToResult;
    }

    public Object getValue(String key) {
        return jsonMap.get(key);
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
        return (String) jsonMap.get("error");
    }

    public Map getJsonMap() {
        return jsonMap;
    }

    public void setJsonMap(Map jsonMap) {
        this.jsonMap = jsonMap;
    }

    public <T> T getSourceAsObject(Class<?> clazz) {
        List sourceList = ((List) extractSource());
        if (sourceList.size() > 0)
            return createSourceObject(sourceList.get(0), clazz);
        else
            return null;
    }

    private <T> T createSourceObject(Object source, Class<?> type) {
        Object obj = null;
        try {
            if (source instanceof Map) {
                Gson gson = new Gson();
                String json = gson.toJson(source, Map.class);
                obj = gson.fromJson(json, type);
            } else {
                obj = type.cast(source);
            }

            //Check if JestId is visible
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(JestId.class)) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        if (value == null) {
                            field.set(obj, ((Map) source).get(ES_METADATA_ID));
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
        return (T) obj;
    }

    public <T> T getSourceAsObjectList(Class<?> type) {
        List<Object> objectList = new ArrayList<Object>();
        if (!isSucceeded) return (T) objectList;
        List<Object> sourceList = (List<Object>) extractSource();
        for (Object source : sourceList) {
            Object obj = createSourceObject(source, type);
            if (obj != null) objectList.add(obj);
        }
        return (T) objectList;
    }

    protected Object extractSource() {
        List<Object> sourceList = new ArrayList<Object>();
        if (jsonMap == null) return sourceList;
        String[] keys = getKeys();
        if (keys == null) {
            sourceList.add(jsonMap);
            return sourceList;
        }
        String sourceKey = keys[keys.length - 1];
        Object obj = jsonMap.get(keys[0]);
        if (keys.length > 1) {
            for (int i = 1; i < keys.length - 1; i++) {
                obj = ((Map) obj).get(keys[i]);
            }
            if (obj instanceof Map) {
                Map<String, Object> source = (Map<String, Object>) ((Map) obj).get(sourceKey);
                if (source != null) sourceList.add(source);
            } else if (obj instanceof List) {
                for (Object newObj : (List) obj) {
                    if (newObj instanceof Map) {
                        Map<String, Object> source = (Map<String, Object>) ((Map) newObj).get(sourceKey);
                        if (source != null) {
                            source.put(ES_METADATA_ID, ((Map) newObj).get("_id"));
                            sourceList.add(source);
                        }
                    }
                }
            }
        } else {
            if (obj != null) {
                sourceList.add(obj);
            }
        }
        return sourceList;
    }

    protected String[] getKeys() {
        return pathToResult == null ? null : (pathToResult + "").split("/");
    }

    public <T> List<T> getFacets(Class<?> type) {
        List<T> facets = new ArrayList<T>();
        if (jsonMap != null) {
            Constructor c;
            try {
                Map<String, Map> facetsMap = (Map<String, Map>) jsonMap.get("facets");
                for (Object facet : facetsMap.keySet()) {
                    c = Class.forName(type.getName()).getConstructor(String.class, Map.class);
                    facets.add((T) c.newInstance(facet.toString(), facetsMap.get(facet)));
                }
                return facets;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return facets;
    }
}
