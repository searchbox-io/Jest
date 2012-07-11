package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * @author Dogukan Sonmez
 */


public class Get extends AbstractAction implements Action {

    protected Get() { }

    public Get(String indexName, String typeName, String id) {
        super.indexName = indexName;
        super.typeName = typeName;
        super.id = id;
        setRestMethodName("GET");
    }

    public Get(String typeName, String id) {
        setDefaultIndexEnabled(true);
        setRestMethodName("GET");
        super.typeName = typeName;
        super.id = id;
    }

    public Get(Doc doc) {
        if (doc.getFields().size() > 0) {
            setURI("_mget");
            List<Doc> docs = new ArrayList<Doc>();
            docs.add(doc);
            setData(prepareMultiGet(docs));
            setBulkOperation(true);
            setRestMethodName("POST");
        } else {
            super.indexName = doc.getIndex();
            super.typeName = doc.getType();
            super.id = doc.getId();
            setRestMethodName("GET");
        }
    }

    public Get(List<Doc> docs) {
        setURI("_mget");
        setBulkOperation(true);
        setRestMethodName("POST");
        setData(prepareMultiGet(docs));
    }

    public Get(String type, String[] ids) {
        setDefaultIndexEnabled(true);
        setRestMethodName("POST");
        setBulkOperation(true);
        setURI("/" + type + "/_mget");
        setData(prepareMultiGet(ids));
    }

    public Get(String[] ids) {
        setDefaultIndexEnabled(true);
        setDefaultTypeEnabled(true);
        setURI("/_mget");
        setData(prepareMultiGet(ids));
        setRestMethodName("POST");
        setBulkOperation(true);
    }

    @Override
    public String getName() {
        return "GET";
    }

    protected Object prepareMultiGet(List<Doc> docs) {
        //[{"_index":"twitter","_type":"tweet","_id":"1","fields":["field1","field2"]}
        StringBuilder sb = new StringBuilder("{\"docs\":[");
        for (Doc doc : docs) {
            sb.append("{\"_index\":\"")
                    .append(doc.getIndex())
                    .append("\",\"_type\":\"")
                    .append(doc.getType())
                    .append("\",\"_id\":\"")
                    .append(doc.getId())
                    .append("\"");
            if (doc.getFields().size() > 0) {
                sb.append(",");
                sb.append(getFieldsString(doc.getFields()));
            }
            sb.append("}");
            sb.append(",");
        }
        sb.delete(sb.toString().length() - 1, sb.toString().length());
        sb.append("]}");
        return sb.toString();
    }

    private Object getFieldsString(HashSet<String> fields) {
        //"fields":["field1","field2"]
        StringBuilder sb = new StringBuilder("\"fields\":[");
        for (String val : fields) {
            sb.append("\"")
                    .append(val)
                    .append("\"")
                    .append(",");
        }
        sb.delete(sb.toString().length() - 1, sb.toString().length());
        sb.append("]");
        return sb.toString();
    }

    protected Object prepareMultiGet(String[] ids) {
        //{"docs":[{"_id":"1"},{"_id" : "2"},{"_id" : "3"}]}
        StringBuilder sb = new StringBuilder("{\"docs\":[")
                .append(concatenateArray(ids))
                .append("]}");
        return sb.toString();
    }

    private String concatenateArray(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (String val : values) {
            sb.append("{\"_id\":\"")
                    .append(val)
                    .append("\"}")
                    .append(",");
        }
        sb.delete(sb.toString().length() - 1, sb.toString().length());
        return sb.toString();
    }

    public String getURI() {
        if (isBulkOperation()) {
            return super.getURI();
        } else {
            return buildURI(indexName, typeName, id);
        }
    }

}
