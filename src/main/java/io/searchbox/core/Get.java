package io.searchbox.core;

import com.google.gson.internal.StringMap;
import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.common.Unicode;
import org.elasticsearch.common.io.stream.BytesStreamOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * @author Dogukan Sonmez
 */


public class Get extends AbstractAction implements Action {

    protected Get() {
    }

    public Get(String indexName, String typeName, String id) {
        super.indexName = indexName;
        super.typeName = typeName;
        super.id = id;
        setRestMethodName("GET");
        setPathToResult("_source");
    }

    public Get(String typeName, String id) {
        setDefaultIndexEnabled(true);
        setRestMethodName("GET");
        super.typeName = typeName;
        super.id = id;
        setPathToResult("_source");
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
        setPathToResult("_source");
    }

    public Get(List<Doc> docs) {
        setURI("_mget");
        setBulkOperation(true);
        setRestMethodName("POST");
        setData(prepareMultiGet(docs));
        setPathToResult("docs/_source");
    }

    public Get(String type, String[] ids) {
        setDefaultIndexEnabled(true);
        setRestMethodName("POST");
        setBulkOperation(true);
        setURI("/" + type + "/_mget");
        setData(prepareMultiGet(ids));
        setPathToResult("docs/_source");
    }

    public Get(String[] ids) {
        setDefaultIndexEnabled(true);
        setDefaultTypeEnabled(true);
        setURI("/_mget");
        setData(prepareMultiGet(ids));
        setRestMethodName("POST");
        setBulkOperation(true);
        setPathToResult("docs/_source");
    }

    public Get(ActionRequest request) {
        GetRequest getRequest = (GetRequest) request;
        super.indexName = getRequest.index();
        super.typeName = getRequest.type();
        super.id = getRequest.id();
        setRestMethodName("GET");
        setPathToResult("_source");
    }

    @Override
    public String getName() {
        return "GET";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        BytesStreamOutput out = new BytesStreamOutput();

        out.writeUTF((String) jsonMap.get("_index"));
        out.writeOptionalUTF((String) jsonMap.get("_type"));
        out.writeUTF((String) jsonMap.get("_id"));

        if (jsonMap.containsKey("_version")) {
            out.writeLong(((Double) jsonMap.get("_version")).longValue());
        } else {
            out.writeLong(0L);
        }

        Boolean exists = (Boolean) jsonMap.get("exists");
        out.writeBoolean(exists);
        if (exists) {
            out.writeBytesHolder(Unicode.fromStringAsBytes(jsonMap.get("_source").toString()), 0, Unicode.fromStringAsBytes(jsonMap.get("_source").toString()).length);

            if (jsonMap.containsKey("fields")) {
                StringMap fields = (StringMap) jsonMap.get("fields");
                out.writeVInt(fields.size());

                for (Object key : fields.keySet()) {
                    out.writeUTF((String) key);
                    List<StringMap> fieldValues = (List) fields.get(key);
                    out.writeVInt(fieldValues.size());
                    for (Object fieldValue : fieldValues) {
                        out.writeGenericValue(fieldValue);
                    }
                }
            } else {
                //no fields provided for query
                out.writeVInt(0);
            }
        }

        return out.copiedByteArray();
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
