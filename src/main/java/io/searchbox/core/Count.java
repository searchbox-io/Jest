package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.support.broadcast.BroadcastOperationThreading;
import org.elasticsearch.common.BytesHolder;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.index.query.QueryBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class Count extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Count.class.getName());

    final private LinkedHashSet<String> indexSet = new LinkedHashSet<String>();

    final private LinkedHashSet<String> typeSet = new LinkedHashSet<String>();

    public Count(String query) {
        setRestMethodName("POST");
        setData(query.toString());
        setPathToResult("count");
    }

    public Count(QueryBuilder query) {
        setRestMethodName("POST");
        setData(query.toString());
        setPathToResult("count");
    }

    protected Count() {
    }

    public Count(ActionRequest request) throws IOException {
        CountRequest countRequest = (CountRequest) request;

        BytesStreamOutput out = new BytesStreamOutput();
        countRequest.writeTo(out);
        BytesStreamInput in = new BytesStreamInput(out.copiedByteArray(), true);

        int size = in.readVInt();
        String[] indices;

        if (size == 0) {
            indices = Strings.EMPTY_ARRAY;
        } else {
            indices = new String[size];
            for (int i = 0; i < indices.length; i++) {
                indices[i] = in.readUTF();
            }
        }
        BroadcastOperationThreading operationThreading = BroadcastOperationThreading.fromId(in.readByte());

        Float minScore = in.readFloat();

        if (in.readBoolean()) {
            String queryHint = in.readUTF();
        }
        if (in.readBoolean()) {
            String routing = in.readUTF();
        }

        BytesHolder bytes = in.readBytesReference();
        boolean querySourceUnsafe = false;
        byte[] querySource = bytes.bytes();
        int querySourceOffset = bytes.offset();
        int querySourceLength = bytes.length();

        String[] types = {};
        int typesSize = in.readVInt();
        if (typesSize > 0) {
            types = new String[typesSize];
            for (int i = 0; i < typesSize; i++) {
                types[i] = in.readUTF();
            }
        }

        indexSet.addAll(Arrays.asList(indices));
        typeSet.addAll(Arrays.asList(types));

        setData(XContentHelper.convertToJson(querySource, querySourceOffset, querySourceLength, false));

        setRestMethodName("POST");
        setPathToResult("count");
    }

    public void addIndex(String index) {
        if (StringUtils.isNotBlank(index)) indexSet.add(index);
    }

    public void addType(String type) {
        if (StringUtils.isNotBlank(type)) typeSet.add(type);
    }

    public boolean removeIndex(String index) {
        return indexSet.remove(index);
    }

    public boolean removeType(String type) {
        return typeSet.remove(type);
    }

    public void clearAllIndex() {
        indexSet.clear();
    }

    public void clearAllType() {
        typeSet.clear();
    }

    public void addIndex(Collection<String> index) {
        indexSet.addAll(index);
    }

    public void addType(Collection<String> type) {
        typeSet.addAll(type);
    }

    public boolean isIndexExist(String index) {
        return indexSet.contains(index);
    }

    public boolean isTypeExist(String type) {
        return typeSet.contains(type);
    }

    public int indexSize() {
        return indexSet.size();
    }

    public int typeSize() {
        return typeSet.size();
    }

    public String getURI() {
        StringBuilder sb = new StringBuilder();
        String indexQuery = createQueryString(indexSet);
        String typeQuery = createQueryString(typeSet);
        if (indexQuery.length() != 0) {
            sb.append(indexQuery).append("/");
            if (typeQuery.length() > 0) {
                sb.append(typeQuery).append("/");
            }
        }
        sb.append("_count");
        log.debug("Created URI for count action is : " + sb.toString());
        return sb.toString();
    }

    protected String createQueryString(LinkedHashSet<String> set) {
        StringBuilder sb = new StringBuilder();
        String tmp = "";
        for (String index : set) {
            sb.append(tmp);
            sb.append(index);
            tmp = ",";
        }
        return sb.toString();
    }

    @Override
    public String getName() {
        return "COUNT";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        BytesStreamOutput out = new BytesStreamOutput();
        out.writeVInt(((Double) jsonMap.get("totalShards")).intValue());
        out.writeVInt(((Double) jsonMap.get("successfulShards")).intValue());
        out.writeVInt(((Double) jsonMap.get("failedShards")).intValue());
        out.writeVInt(((Double) jsonMap.get("totalShards")).intValue());
        out.writeBoolean(((Boolean) jsonMap.get("found")));
        return out.copiedByteArray();
    }
}
