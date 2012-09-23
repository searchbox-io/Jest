package io.searchbox.client;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class JestResult {

    private static Logger log = Logger.getLogger(JestResult.class.getName());

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
                        if (source != null) sourceList.add(source);
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

}
