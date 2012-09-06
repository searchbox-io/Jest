package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class MultiGet extends AbstractAction implements Action {

    protected MultiGet(){}

    public MultiGet(List<Doc> docs) {
        setURI("_mget");
        setBulkOperation(true);
        setRestMethodName("POST");
        setData(prepareMultiGet(docs));
        setPathToResult("docs/_source");
    }

    public MultiGet(String type, String[] ids) {
        setRestMethodName("POST");
        setBulkOperation(true);
        setURI("/" + type + "/_mget");
        setData(prepareMultiGet(ids));
        setPathToResult("docs/_source");
    }

    public MultiGet(String[] ids) {
        setURI("_mget");
        setData(prepareMultiGet(ids));
        setRestMethodName("POST");
        setBulkOperation(true);
        setPathToResult("docs/_source");
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



    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPathToResult() {
        return "docs/_source";
    }
}
