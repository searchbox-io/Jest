package io.searchbox.core;

import com.google.gson.internal.StringMap;
import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.common.Unicode;
import org.elasticsearch.common.io.stream.BytesStreamOutput;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * @author Dogukan Sonmez
 */


public class Get extends AbstractAction implements Action {

    public static class Builder {
        private String index = null;
        private String type = null;
        private final String id;

        public Builder(String id) {
            this.id = id;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Get build() {
            return new Get(this);
        }
    }

    private Get(Builder builder) {
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
        prepareGet(indexName,typeName);
    }

    public Get(ActionRequest request) {
        GetRequest getRequest = (GetRequest) request;
        super.indexName = getRequest.index();
        super.typeName = getRequest.type();
        super.id = getRequest.id();
        prepareGet(indexName,typeName);
    }

    private void prepareGet(String indexName, String typeName) {
        if(StringUtils.isBlank(indexName)) setDefaultIndexEnabled(true);
        if(StringUtils.isBlank(typeName)) setDefaultTypeEnabled(true);
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

    @Override
    public String getName() {
        return "GET";
    }

    @Override
    public String getURI() {
        return buildURI(indexName, typeName, id);
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    public String getPathToResult() {
        return "_source";
    }
}
