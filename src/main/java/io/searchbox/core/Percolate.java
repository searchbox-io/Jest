package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.common.Unicode;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.xcontent.XContentHelper;

import java.io.IOException;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class Percolate extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Percolate.class.getName());

    public Percolate(String indexName, String type, Object query) {
        setURI(buildGetURI(indexName, type));
        setData(query);
        setRestMethodName("POST");
    }

    public Percolate(ActionRequest request) throws IOException {
        PercolateRequest percolateRequest = (PercolateRequest) request;
        setURI(buildGetURI(percolateRequest.index().toLowerCase(), percolateRequest.type().toLowerCase()));
        setData(XContentHelper.convertToJson(percolateRequest.source(), 0, percolateRequest.source().length, false));
        setRestMethodName("POST");
    }

    private String buildGetURI(String indexName, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI(indexName,type,null))
                .append("/")
                .append("_percolate");
        log.debug("Created URI for update action is :" + sb.toString());
        return sb.toString();
    }

    @Override
    public String getName() {
        return "PERCOLATE";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        BytesStreamOutput out = new BytesStreamOutput();
        out.writeBoolean(false);
        out.writeUTF((String) jsonMap.get("_index"));
        out.writeUTF((String) jsonMap.get("_type"));
        out.writeBytesHolder(Unicode.fromStringAsBytes(jsonMap.get("_source").toString()), 0, Unicode.fromStringAsBytes(jsonMap.get("_source").toString()).length);
        return out.copiedByteArray();
    }
}
