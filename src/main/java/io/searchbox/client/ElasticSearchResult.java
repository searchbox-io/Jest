package io.searchbox.client;

import net.sf.json.JSONObject;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchResult {

    private JSONObject json;

    private boolean isSucceeded;


    public Object getResultValue(String key){
         return  json.get(key);
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public boolean isSucceeded() {
        return isSucceeded;
    }

    public void setSucceeded(boolean succeeded) {
        isSucceeded = succeeded;
    }

    public String getIndexName(){
      return json.getString("_index");
    }

    public String getType(){
        return json.getString("_type");
    }

    public String getId(){
        return json.getString("_id");
    }

    public Object getSource(){
        return json.get("_source");
    }

    public String getErrorMessage(){
        return json.getString("error");
    }
}
