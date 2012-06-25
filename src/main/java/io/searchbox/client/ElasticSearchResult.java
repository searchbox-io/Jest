package io.searchbox.client;

import io.searchbox.Document;

import java.util.List;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchResult {

    private Map jsonMap;

    private String jsonString;

    private boolean isSucceeded;

    private List<Document> documents;


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

    public String getErrorMessage() {
        return (String) jsonMap.get("error");
    }

    public Map getJsonMap() {
        return jsonMap;
    }

    public void setJsonMap(Map jsonMap) {
        this.jsonMap = jsonMap;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
