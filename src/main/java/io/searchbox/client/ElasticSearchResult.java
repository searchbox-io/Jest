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

    public Object getSourceAsObject(Class<?> type){
        List<Object> sourceList = (List<Object>) extractSource();
        Object source = sourceList.get(0);
        if(source instanceof Map){
            Object obj = null;
            try {
                obj  =type.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            for(Object key:((Map) source).keySet()){

            }

        }else{
            return type.cast(source);
        }
       return null;
    }

    public <T> T getSourceAsObjectList(Class<?> type) {
        return null;
    }

    protected Object extractSource() {
        List<Object> sourceList = new ArrayList<Object>();
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
