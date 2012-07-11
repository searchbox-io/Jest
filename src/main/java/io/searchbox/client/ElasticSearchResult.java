package io.searchbox.client;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchResult {

    private Map jsonMap;

    private String jsonString;

    private boolean isSucceeded;

    public Object getValue(String key) {
        return jsonMap.get(key);
    }

    public String getJson() {
        return jsonString;
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

    public String getSourceAsString(){
        return null;
    }

    public Map getSourceAsMap(){
        return null;
    }

    public Object getSourceAsObject(Class<?> type){
        return null;
    }
}
