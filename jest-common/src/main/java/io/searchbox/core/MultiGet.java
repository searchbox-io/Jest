package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.AbstractAction;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class MultiGet extends AbstractAction {

    private Object source;

    public MultiGet(Builder.ByDoc builder) {
        super(builder);

        this.source = prepareMultiGet(builder.docs);
        setCommonActionParameters();
    }

    public MultiGet(Builder.ById builder) {
        super(builder);
        indexName = builder.index;
        typeName = builder.type;

        this.source = prepareMultiGet(builder.ids.toArray(new String[0]));
        setCommonActionParameters();
    }

    protected static Object prepareMultiGet(List<Doc> docs) {
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

    private static Object getFieldsString(HashSet<String> fields) {
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

    protected static Object prepareMultiGet(String[] ids) {
        //{"docs":[{"_id":"1"},{"_id" : "2"},{"_id" : "3"}]}
        StringBuilder sb = new StringBuilder("{\"docs\":[")
                .append(concatenateArray(ids))
                .append("]}");
        return sb.toString();
    }

    private static String concatenateArray(String[] values) {
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
    public Object getData(Gson gson) {
        return source;
    }

    private void setCommonActionParameters() {
        setBulkOperation(true);
        setURI(buildURI());
        setPathToResult("docs/_source");
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_mget");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder {
        private Builder() {
        }

        public static class ById extends AbstractAction.Builder<MultiGet, ById> {
            private String index;
            private String type;
            private List<String> ids = new LinkedList<String>();

            public ById(String index, String type) {
                this.index = index;
                this.type = type;
            }

            public ById addId(Collection<? extends String> ids) {
                this.ids.addAll(ids);
                return this;
            }

            public ById addId(String id) {
                ids.add(id);
                return this;
            }

            @Override
            public MultiGet build() {
                return new MultiGet(this);
            }
        }

        public static class ByDoc extends AbstractAction.Builder<MultiGet, ByDoc> {
            private List<Doc> docs = new LinkedList<Doc>();

            public ByDoc(Collection<? extends Doc> docs) {
                this.docs.addAll(docs);
            }

            public ByDoc(Doc doc) {
                docs.add(doc);
            }

            public ByDoc addDoc(Collection<? extends Doc> docs) {
                this.docs.addAll(docs);
                return this;
            }

            public ByDoc addDoc(Doc doc) {
                docs.add(doc);
                return this;
            }

            @Override
            public MultiGet build() {
                return new MultiGet(this);
            }
        }
    }
}
