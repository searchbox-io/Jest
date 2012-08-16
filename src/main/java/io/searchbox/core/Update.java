package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.support.replication.ReplicationType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.unit.TimeValue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class Update extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Update.class.getName());

    public Update(Doc doc, Object script) {
        setURI(buildURI(doc));
        setRestMethodName("POST");
        setData(script);
    }

    public Update(String indexName, String typeName, String id, Object script) {
        setURI(buildURI(new Doc(indexName, typeName, id)));
        setRestMethodName("POST");
        setData(script);
    }

    public Update(ActionRequest request) throws IOException {
        UpdateRequest updateRequest = (UpdateRequest) request;

        BytesStreamOutput output = new BytesStreamOutput();
        updateRequest.writeTo(output);
        BytesStreamInput input = new BytesStreamInput(output.copiedByteArray(), true);

        String index = input.readUTF();
        int shardId = input.readInt();
        TimeValue timeout = TimeValue.readTimeValue(input);

        ReplicationType replicationType = ReplicationType.fromId(input.readByte());
        WriteConsistencyLevel consistencyLevel = WriteConsistencyLevel.fromId(input.readByte());

        String type = input.readUTF();
        String id = input.readUTF();
        if (input.readBoolean()) {
            String routing = input.readUTF();
        }
        String script = input.readUTF();
        if (input.readBoolean()) {
            String scriptLang = input.readUTF();
        }
        Map<String, Object> scriptParams = input.readMap();
        int retryOnConflict = input.readVInt();
        if (input.readBoolean()) {
            String percolate = input.readUTF();
        }
        boolean refresh = input.readBoolean();

        setURI(buildURI(new Doc(index, type, id)));

        //create script
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"script\" : \" ").append(script).append("\"");

        if (scriptParams.size() > 0) {
            sb.append(", \"params\" : {");

            for (String key : scriptParams.keySet()) {
                Object value = scriptParams.get(key);
                sb.append(", \"").append(key).append("\" : ").append("\"").append(value).append("\",\n");
            }

            sb.append("\"}\n");
        }

        sb.append("\"}\n");
        setData(sb.toString());

        setRestMethodName("POST");
    }

    protected String buildURI(Doc doc) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildURI(doc.getIndex(), doc.getType(), doc.getId()))
                .append("/")
                .append("_update");
        log.debug("Created URI for update action is :" + sb.toString());
        return sb.toString();
    }

    @Override
    public String getName() {
        return "UPDATE";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        BytesStreamOutput output = new BytesStreamOutput();

        output.writeUTF((String) jsonMap.get("_index"));
        output.writeUTF((String) jsonMap.get("_id"));
        output.writeUTF((String) jsonMap.get("_type"));
        output.writeLong(((Double) jsonMap.get("_version")).longValue());

        if (jsonMap.containsKey("matches")) {
            output.writeBoolean(true);
            List<String> matches = (List) jsonMap.get("matches");
            output.writeVInt(matches.size());
            for (String match : matches) {
                output.writeUTF(match);
            }

        } else {
            output.writeBoolean(false);
        }

        return output.copiedByteArray();
    }
}
