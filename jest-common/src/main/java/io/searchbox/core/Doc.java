package io.searchbox.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Represents a single get request description in a MultiGet request.
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Doc {

    private final String index;

    private final String type;

    private final String id;

    private final HashSet<String> fields = new LinkedHashSet<String>();

    public Doc(String index, String type, String id) {
        this.index = index;
        this.type = type;
        this.id = id;
    }

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public HashSet<String> getFields() {
        return fields;
    }

    public void addFields(Collection<String> fields) {
        this.fields.addAll(fields);
    }

    public void addField(String field) {
        fields.add(field);
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    public String toJsonString() {
        //[{"_index":"twitter","_type":"tweet","_id":"1","fields":["field1","field2"]}
        StringBuilder sb = new StringBuilder();
        sb.append("{\"_index\":\"")
                .append(getIndex())
                .append("\",\"_type\":\"")
                .append(getType())
                .append("\",\"_id\":\"")
                .append(getId())
                .append("\"");
        if (!getFields().isEmpty()) {
            sb.append(",");
            sb.append(fieldsToJsonString());
        }
        sb.append("}");
        return sb.toString();
    }

    private String fieldsToJsonString() {
        //"fields":["field1","field2"]
        StringBuilder sb = new StringBuilder("\"fields\":[");
        for (String val : getFields()) {
            sb.append("\"")
                    .append(val)
                    .append("\"")
                    .append(",");
        }
        sb.delete(sb.toString().length() - 1, sb.toString().length());
        sb.append("]");
        return sb.toString();
    }

}
