package io.searchbox.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * @author Dogukan Sonmez
 */
public class Doc {

    private String index;

    private String type;

    private String id;

    private final HashSet<String> fields = new LinkedHashSet<String>();

    public Doc(String index, String type, String id) {
        this.index = index;
        this.type = type;
        this.id = id;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
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

    public HashSet<String> getFields() {
        return fields;
    }

    public void addFields(Collection<String> fields) {
        this.fields.addAll(fields);
    }

    public void removeAllFields() {
        fields.clear();
    }

    public boolean removeField(String field) {
        return fields.remove(field);
    }

    public void addField(String field) {
        fields.add(field);
    }
}
