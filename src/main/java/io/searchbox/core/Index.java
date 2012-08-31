package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.io.stream.BytesStreamOutput;

import java.io.IOException;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class Index extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Index.class.getName());

    private final Object source;

    public static class Builder {
        private String index = null;
        private String type = null;
        private String id = null;
        private final Object source;

        public Builder(Object source) {
            this.source = source;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Index build() {
            return new Index(this);
        }
    }

    private Index(Builder builder) {
        source = builder.source;
        prepareIndex(builder.index,builder.type,builder.id);
    }
    public Index(ActionRequest request) {
        IndexRequest indexRequest = (IndexRequest) request;
        String indexName = indexRequest.index();
        String type = indexRequest.type();
        source = indexRequest.source();
        String id = indexRequest.id();
        prepareIndex(indexName,type,id);
        setData(source);
    }

    private void prepareIndex(String indexName, String typeName, String id) {
        if (indexName != null) {
            super.indexName = indexName;
        } else {
            setDefaultIndexEnabled(true);
        }

        if (typeName != null) {
            super.typeName = typeName;
        } else {
            setDefaultTypeEnabled(true);
        }
        if (id != null) {
            super.id = id;
            setRestMethodName("PUT");
            setURI(buildURI(indexName, typeName, id));
        } else {
            setRestMethodName("POST");
        }
    }

    // See IndexResponse.readFrom to understand how to create output
    public byte[] createByteResult(Map jsonMap) throws IOException {
        BytesStreamOutput output = new BytesStreamOutput();
        output.writeUTF((String) jsonMap.get("_index"));
        output.writeUTF((String) jsonMap.get("_id"));
        output.writeUTF((String) jsonMap.get("_type"));
        output.writeLong(((Double) jsonMap.get("_version")).longValue());
        output.writeBoolean(false);
        return output.copiedByteArray();
    }


    /* Need to call buildURI method each time to check if new parameter added*/
    @Override
    public String getURI() {
        return buildURI(indexName, typeName, id);
    }

    @Override
    public String getName() {
        return "INDEX";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }
}
