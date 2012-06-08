package io.searchbox;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dogukan Sonmez
 */


public class Index {

    private String name;

    private String type;

    private String id;

    private List<String> fields;

    public Index(String name, String type, String id) {
        this.name = name;
        this.type = type;
        this.id = id;
    }

    public Index(String name, String type){
        this.name = name;
        this.type = type;
    }


    public Index(String name){
        this.name = name;
    }

    public Index(String name, String type, String id,List<String> fields) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.fields = fields;
    }

    public Index(){

    }

    public void addField(String field) {
        this.fields.add(field);
    }

    public List<String> getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

}
