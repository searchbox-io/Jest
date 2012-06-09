package io.searchbox;

import java.util.HashMap;
import java.util.List;

/**
 * @author Dogukan Sonmez
 */


public class Document {

    private String indexName;

    private String type;

    private String id;

    private Object source;

    private List<String> fields;

    private final HashMap<String, Object> settings = new HashMap<String, Object>();

    public Document(String indexName, String type, String id){
        this.indexName = indexName;
        this.type = type;
        this.id = id;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
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

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
