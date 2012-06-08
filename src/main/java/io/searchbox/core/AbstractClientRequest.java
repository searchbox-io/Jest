package io.searchbox.core;

import io.searchbox.Index;
import io.searchbox.core.settings.Settings;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * @author Dogukan Sonmez
 */


public class AbstractClientRequest implements ClientRequest {

    private final HashMap<String, Object> settings = new HashMap<String, Object>();

    private Object data;

    private String URI;

    private String restMethodName;

    public void setRestMethodName(String restMethodName) {
        this.restMethodName = restMethodName;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void addSetting(String name, Object value){
        settings.put(name,value);
    }

    public Object getSettingValue(String name){
        return settings.get(name);
    }

    public HashMap<String, Object> getSettings() {
        return settings;
    }

    public String getURI() {
       return URI;
    }

    public String getRestMethodName() {
        return restMethodName;
    }


    public Object getData() {
        return data;
    }

    protected String buildURI(Index index) {
        StringBuilder sb = new StringBuilder();
        sb.append(index.getName())
                .append("/")
                .append(index.getType())
                .append("/").
                append(index.getId());
        return sb.toString();
    }
}
