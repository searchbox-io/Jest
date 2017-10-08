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

    private String routing;

    private Object source;

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

    /**
     * Specific stored fields can be specified to be retrieved per document to get,
     * similar to the fields parameter of the Get API.
     */
    public void addFields(Collection<String> fields) {
        this.fields.addAll(fields);
    }

    /**
     * Specific stored fields can be specified to be retrieved per document to get,
     * similar to the fields parameter of the Get API.
     */
    public void addField(String field) {
        fields.add(field);
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public String getRouting() {
        return routing;
    }

    /**
     * By default, the _source field will be returned for every document (if stored).
     * Similar to the get API, you can retrieve only parts of the _source (or not at all)
     * by using the _source parameter. You can also use the url parameters _source,
     * _source_include & _source_exclude to specify defaults, which will be used when there
     * are no per-document instructions.
     *
     */
    public void setSource(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    protected Map<String, Object> toMap() {
        Map<String, Object> retval = new LinkedHashMap<String, Object>();

        retval.put("_index", index);
        retval.put("_type", type);
        retval.put("_id", id);

        if(!fields.isEmpty()) {
            retval.put("fields", fields);
        }

        if(StringUtils.isNotEmpty(routing)){
            retval.put("_routing", routing);
        }

        if(source != null) {
            retval.put("_source", source);
        }

        return retval;
    }
}
