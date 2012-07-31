package io.searchbox.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchResult {

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

    public Object getSourceAsObject(Class<?> type) {
        return null;
    }

    public <T> T getSourceAsObjectList(Class<?> type) {
        return null;
    }

    protected List<Map<String, Object>> extractSource() {
        String[] keys = getKeys();
        Object obj = jsonMap.get(keys[0]);
        if (obj instanceof Map) {
            List source = new ArrayList();
            source.add(obj);
            return source;
        } else if(obj instanceof List){
            return (List<Map<String, Object>>) obj;
        }else {
            return null;
        }

    }

    protected String[] getKeys() {
        return (pathToResult + "").split("/");
    }

    public Map<String, Object> getSourceAsMap() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
