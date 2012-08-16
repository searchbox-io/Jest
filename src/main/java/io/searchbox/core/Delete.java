package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Dogukan Sonmez
 */


public class Delete extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Delete.class.getName());

    protected Delete() {

    }

    public Delete(String indexName) {
        super.indexName = indexName;
        setRestMethodName("DELETE");
    }

    public Delete(String indexName, String typeName) {
        super.indexName = indexName;
        super.typeName = typeName;
        setRestMethodName("DELETE");
    }

    public Delete(String indexName, String typeName, String id) {
        super.indexName = indexName;
        super.typeName = typeName;
        super.id = id;
        setURI(buildURI(indexName, typeName, id));
        setRestMethodName("DELETE");
    }

    public Delete(Doc doc) {
        super.indexName = doc.getIndex();
        super.typeName = doc.getType();
        super.id = doc.getId();
        setRestMethodName("DELETE");
    }

    public Delete(List<Doc> docs) {
        setRestMethodName("POST");
        super.indexName = "_bulk";
        setBulkOperation(true);
        setData(prepareBulkForDelete(docs));
    }

    public Delete(String[] ids) {
        setDefaultIndexEnabled(true);
        setDefaultTypeEnabled(true);
        setRestMethodName("POST");
        super.indexName = "_bulk";
        setBulkOperation(true);
        List<Doc> docs = createDocList(ids);
        setData(prepareBulkForDelete(docs));
    }

    protected List<Doc> createDocList(String[] ids) {
        List<Doc> docList = new ArrayList<Doc>();
        for (String id : ids) docList.add(new Doc("<jesttempindex>", "<jesttemptype>", id));
        return docList;
    }

    public String getName() {
        return "DELETE";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];
    }

    protected Object prepareBulkForDelete(List<Doc> docs) {
        //Example bulk: { "delete" : { "_index" : "twitter", "_type" : "tweet", "_id" : "1" } }
        StringBuilder sb = new StringBuilder();
        for (Doc doc : docs) {
            sb.append("{ \"delete\" : { \"_index\" : \"")
                    .append(doc.getIndex())
                    .append("\", \"_type\" : \"")
                    .append(doc.getType())
                    .append("\", \"_id\" : \"")
                    .append(doc.getId())
                    .append("\" } }\n");
        }
        return sb.toString();
    }

    public String getURI() {
        return buildURI(indexName, typeName, id);
    }
}
