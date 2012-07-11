package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author Dogukan Sonmez
 */


public class Index extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Index.class.getName());

    protected Index() {
    }

    public Index(String indexName, String typeName, String id, Object source) {
        setRestMethodName("PUT");
        setData(source);
        super.indexName = indexName;
        super.typeName = typeName;
        super.id = id;
        setURI(buildURI(indexName, typeName, id));
    }

    public Index(String indexName, String typeName, Object source) {
        setRestMethodName("POST");
        setData(source);
        super.indexName = indexName;
        super.typeName = typeName;
    }

    public Index(String indexName, String typeName, List<Object> sources) {
        setRestMethodName("POST");
        setData(prepareBulkForIndex(sources, indexName, typeName));
        setBulkOperation(true);
        super.indexName = "_bulk";
    }

    public Index(String typeName, Object source, String id) {
        setDefaultIndexEnabled(true);
        setRestMethodName("PUT");
        setData(source);
        super.typeName = typeName;
        super.id = id;
    }

    public Index(String typeName, Object source) {
        setDefaultIndexEnabled(true);
        setRestMethodName("POST");
        setData(source);
        super.typeName = typeName;
    }

    public Index(String typeName, List<Object> sources) {
        setDefaultIndexEnabled(true);
        setRestMethodName("POST");
        setBulkOperation(true);
        setData(prepareBulkForIndex(sources, "<jesttempindex>", typeName));
        super.indexName = "_bulk";
    }

    public Index(Object source, String id) {
        setDefaultIndexEnabled(true);
        setDefaultTypeEnabled(true);
        setRestMethodName("PUT");
        setData(source);
        super.id = id;
    }

    public Index(Object source) {
        setDefaultIndexEnabled(true);
        setDefaultTypeEnabled(true);
        setRestMethodName("POST");
        setData(source);
    }

    public Index(List<Object> sources) {
        setDefaultIndexEnabled(true);
        setDefaultTypeEnabled(true);
        setBulkOperation(true);
        setRestMethodName("POST");
        setData(prepareBulkForIndex(sources, "<jesttempindex>", "<jesttemptype>"));
        super.indexName = "_bulk";
    }

    protected Object prepareBulkForIndex(List<Object> sources, String indexName, String typeName) {
        /*
        { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
        { "field1" : "value1" }
         */
        StringBuilder sb = new StringBuilder();
        for (Object source : sources) {
            sb.append("{ \"index\" : { \"_index\" : \"")
                    .append(indexName)
                    .append("\", \"_type\" : \"")
                    .append(typeName)
                    .append("\"}}\n");
            sb.append(getJson(source));
            sb.append("\n");
        }
        return sb.toString();
    }

    private Object getJson(Object source) {
        return new Gson().toJson(source);
    }

    @Override
    public String getName() {
        return "INDEX";
    }

    public String getURI() {
        return buildURI(indexName,typeName,id);
    }

}
