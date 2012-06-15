package io.searchbox.client;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchResult {

    private Map jsonMap;

    private String jsonString;

    private boolean isSucceeded;


    public Object getResultValue(String key) {
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

    public String getIndexName() {
        return (String) jsonMap.get("_index");
    }

    public String getType() {
        return (String) jsonMap.get("_type");
    }

    public String getId() {
        return (String) jsonMap.get("_id");
    }

    public Object getSource() {
        return jsonMap.get("_source");
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
}
