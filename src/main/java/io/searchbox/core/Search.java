package io.searchbox.core;


import com.google.gson.internal.StringMap;
import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.util.CollectionUtils;

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

    public Search(ActionRequest request) throws IOException {
        setRestMethodName("POST");

        SearchRequest searchRequest = (SearchRequest) request;
        this.addIndex(Arrays.asList(searchRequest.indices()));
        this.addType(Arrays.asList(searchRequest.types()));

        setData(XContentHelper.convertToJson(searchRequest.source(), 0, searchRequest.source().length, false));

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
        output.writeVLong(((Double) hits.get("total")).longValue());
        output.writeFloat(((Double) hits.get("max_score")).floatValue());
        output.writeVInt(resultHits.size());

        // to escape from StreamContext.ShardTargetType.LOOKUP
        output.writeVInt(0);

        // See InternalSearchHit.readFrom
        for (Map resultHit : resultHits) {
            output.writeFloat(((Double) resultHit.get("_score")).floatValue());
            output.writeUTF((String) resultHit.get("_id"));
            output.writeUTF((String) resultHit.get("_type"));
            //output.writeLong(((Double) hits.get("_version")).longValue());
            output.writeLong(1);
            output.writeBytesHolder(resultHit.get("_source").toString().getBytes(), 0, resultHit.get("_source").toString().getBytes().length);

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

            //InternalSearchHitField.readFrom
            if (resultHit.containsKey("fields")) {
                StringMap fields = (StringMap) resultHit.get("fields");
                output.writeVInt(fields.size());

                for (Object key : fields.keySet()) {
                    output.writeUTF((String) key);
                    List<StringMap> fieldValues = (List) fields.get(key);
                    output.writeVInt(fieldValues.size());
                    for (Object fieldValue : fieldValues) {
                        output.writeGenericValue(fieldValue);
                    }
                }

            } else {
                //no fields provided for query
                output.writeVInt(0);
            }

            //HighlightField.readFrom "highlight":{"name":["<em>Batman</em>"]}}
            if (resultHit.containsKey("highlight")) {
                StringMap highlight = (StringMap) resultHit.get("highlight");

                for (Object key : highlight.keySet()) {
                    output.writeUTF((String) key);
                    List<Object> fragments = (List) highlight.get(key);

                    if (!CollectionUtils.isEmpty(fragments)) {
                        output.writeBoolean(true);
                        output.writeVInt(fragments.size());

                        for (Object fragment : fragments) {
                            output.writeUTF((String) fragment);
                        }
                    } else {
                        output.writeBoolean(false);
                    }
                }
            } else {
                // no highlight
                output.writeVInt(0);
            }

            if (resultHit.containsKey("sort")) {

            } else {
                // no filters
                output.writeVInt(0);
            }

            if (resultHit.containsKey("filters")) {

            } else {
                // no filters
                output.writeVInt(0);
            }

            // escape from InternalSearchHits.StreamContext.ShardTargetType.LOOKUP
            output.writeVInt(0);
        }

        // Facets
        output.writeBoolean(false);

        //Timeout
        output.writeBoolean(false);

        output.writeVInt(((Double) ((Map) jsonMap.get("_shards")).get("total")).intValue());
        output.writeVInt(((Double) ((Map) jsonMap.get("_shards")).get("successful")).intValue());
        output.writeVInt(((Double) ((Map) jsonMap.get("_shards")).get("failed")).intValue());

        if (jsonMap.containsKey("scrollId")) {
            output.writeBoolean(true);
            output.writeUTF((String) jsonMap.get("scrollId"));
        }

        output.writeLong(((Double) jsonMap.get("took")).longValue());

        return output.copiedByteArray();
    }
}
