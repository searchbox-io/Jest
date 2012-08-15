package io.searchbox.core;


import com.google.gson.internal.StringMap;
import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.Unicode;
import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.internal.InternalSearchRequest;

import java.io.IOException;
import java.util.*;


/**
 * @author Dogukan Sonmez
 */


public class Search extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Search.class.getName());

    final private LinkedHashSet<String> indexSet = new LinkedHashSet<String>();

    final private LinkedHashSet<String> typeSet = new LinkedHashSet<String>();

    public Search(QueryBuilder query) {
        setRestMethodName("POST");
        setData(query.toString());
        setPathToResult("hits/hits/_source");
    }

    public Search(ActionRequest request) {
        setRestMethodName("POST");

        SearchRequest searchRequest = (SearchRequest) request;
        this.addIndex(Arrays.asList(searchRequest.indices()));
        this.addType(Arrays.asList(searchRequest.types()));

        try {
            setData(XContentHelper.convertToJson(searchRequest.source(), 0, searchRequest.source().length, false));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setPathToResult("hits/hits/_source");
    }

    protected Search() {
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

        if (indexQuery.length() == 0 && typeQuery.length() > 0) {
            sb.append("_all/").append(typeQuery).append("/");

        } else if (indexQuery.length() > 0 && typeQuery.length() > 0) {
            sb.append(indexQuery).append("/").append(typeQuery).append("/");

        } else if (indexQuery.length() > 0 && typeQuery.length() == 0) {
            sb.append(indexQuery).append("/");
        }

        sb.append("_search");
        log.debug("Created URI for search action is : " + sb.toString());
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
        return "SEARCH";
    }

    // See SearchResponse.readFrom
    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        BytesStreamOutput output = new BytesStreamOutput();

        Map hits = (Map) jsonMap.get("hits");
        List<StringMap> resultHits = (List) (hits.get("hits"));

        // See InternalSearchHits.readFrom
        output.writeVInt(((Double) ((Map) jsonMap.get("_shards")).get("total")).intValue());
        output.writeLong(((Double) hits.get("max_score")).longValue());
        output.writeVInt(resultHits.size());

        // See InternalSearchHit.readFrom
        for (Map resultHit : resultHits) {
            output.writeFloat(((Double) resultHit.get("score")).floatValue());
            output.writeUTF((String) resultHit.get("_id"));
            output.writeUTF((String) resultHit.get("_type"));
            output.writeLong(((Double) hits.get("_version")).longValue());
            output.writeBytes(Unicode.fromStringAsBytes(resultHit.get("_source").toString()));

            //org.elasticsearch.common.lucene.Lucene.readExplanation
            if (resultHit.containsKey("_explanation")) {
                // set there is explanation
                output.writeBoolean(true);

                StringMap explanation = (StringMap) resultHit.get("_explanation");
                output.writeFloat(((Double) explanation.get("value")).floatValue());
                output.writeUTF((String) explanation.get("description"));

                if (explanation.containsValue("details")) {
                    List<StringMap> details = (List) explanation.get("details");
                    output.writeVInt(details.size());
                    for (StringMap detail : details) {
                        output.writeFloat(((Double) detail.get("value")).floatValue());
                        output.writeUTF((String) detail.get("description"));
                        // Setting false to avoid infinite loop at recursive call
                        output.writeBoolean(false);
                    }
                }
            } else {
                //no explanation
                output.writeBoolean(false);
            }


        }

        return output.copiedByteArray();
    }
}
