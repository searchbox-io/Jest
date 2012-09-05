package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.common.io.stream.BytesStreamOutput;

import java.io.IOException;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class Delete extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Delete.class.getName());

    public static class Builder {
        private String index;
        private String type;
        private final String id;

        public Builder(String id){
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
        
        public Delete build() {
            return new Delete(this);
        }
    }

    private Delete(Builder builder) {
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
    }

    public Delete(ActionRequest request) {
        DeleteRequest deleteRequest = (DeleteRequest) request;
        String index = deleteRequest.index();
        String type = deleteRequest.type();
        String id = deleteRequest.id();
        super.indexName = index;
        super.typeName = type;
        super.id = id;
    }

    @Override
    public String getURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(buildURI(indexName, typeName, id));
        String queryString = buildQueryString();
        if (StringUtils.isNotBlank(queryString)) sb.append(queryString);
        return sb.toString();
    }

    @Override
    public String getName() {
        return "DELETE";
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        BytesStreamOutput output = new BytesStreamOutput();
        output.writeUTF((String) jsonMap.get("_index"));
        output.writeUTF((String) jsonMap.get("_id"));
        output.writeUTF((String) jsonMap.get("_type"));
        output.writeLong(((Double) jsonMap.get("_version")).longValue());
        output.writeBoolean(((Boolean) jsonMap.get("found")));
        return output.copiedByteArray();
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

}
