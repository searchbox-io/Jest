package io.searchbox.core;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

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

    private final Collection<String> fields = new LinkedList<String>();

    public Doc(String index, String type, String id) {
        if(StringUtils.isEmpty(index)){
            throw new IllegalArgumentException("Required Index argument cannot be null or empty.");
        }
        if(StringUtils.isEmpty(type)){
            throw new IllegalArgumentException("Required Type argument cannot be null or empty.");
        }
        if(StringUtils.isEmpty(id)){
            throw new IllegalArgumentException("Required Id argument cannot be null or empty.");
        }

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

    public Collection<String> getFields() {
        return fields;
    }

    public void addFields(Collection<String> fields) {
        this.fields.addAll(fields);
    }

    public void addField(String field) {
        fields.add(field);
    }

    protected Map<String, Object> toMap() {
        Map<String, Object> retval = new HashMap<String, Object>();

        retval.put("_index", index);
        retval.put("_type", type);
        retval.put("_id", id);

        if(!fields.isEmpty()) {
            retval.put("fields", fields);
        }

        return retval;
    }

}
