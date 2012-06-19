package io.searchbox;

import com.google.gson.Gson;

/**
 * @author Dogukan Sonmez
 */


public class Source {

    private String json;

    public Source(Object data) {
        this.json =  new Gson().toJson(data);
    }

    @Override
    public String toString() {
        return json;
    }

    public Object getObject(Class<?> type){
        return new Gson().fromJson(json,type);
    }
}
