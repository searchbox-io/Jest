package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Dogukan Sonmez
 */


// TODO Bulk api does not using default index and type yet
public class Bulk extends AbstractAction implements Action {

    private final Set<Index> indexSet = new LinkedHashSet<Index>();

    private final Set<Delete> deleteSet = new LinkedHashSet<Delete>();

    public Bulk() {
        setURI("_bulk");
    }

    public Bulk(String indexName) {
        setURI(buildURIWithoutDefaults(indexName, null, null) + "/_bulk");
    }

    public Bulk(String indexName, String typeName) {
        setURI(buildURIWithoutDefaults(indexName, typeName, null) + "/_bulk");
    }

    public void addIndex(Index index) {
        if (index != null) indexSet.add(index);
    }

    public void addDelete(Delete delete) {
        if (delete != null) deleteSet.add(delete);
    }

    public boolean isIndexExist(Index index) {
        return indexSet.contains(index);
    }

    public boolean isDeleteExist(Delete delete) {
        return deleteSet.contains(delete);
    }

    public void removeIndex(Index index) {
        indexSet.remove(index);
    }

    public void removeDelete(Delete delete) {
        deleteSet.remove(delete);
    }

    protected Object prepareBulk(Set<AbstractAction> indexSet) {
        /*
        { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
        { "field1" : "value1" }
        { "delete" : { "_index" : "test", "_type" : "type1", "_id" : "2" } }
         */
        StringBuilder sb = new StringBuilder();
        for (AbstractAction action : indexSet) {
            sb.append("{ \"");
            sb.append(action.getName().toLowerCase());
            sb.append("\" : { ");

            if (!StringUtils.isBlank(action.getIndexName())) {
                sb.append("\"_index\" : \"");
                sb.append(action.getIndexName());
            }

            if (!StringUtils.isBlank(action.getTypeName())) {
                sb.append("\", \"_type\" : \"");
                sb.append(action.getTypeName());
            }

            if (!StringUtils.isBlank(action.getId())) {
                sb.append("\", \"_id\" : \"")
                        .append(action.getId());
            }

            sb.append("\"}}\n");
            if (action.getName().equalsIgnoreCase("index")) {
                sb.append(getJson(action.getData()));
                sb.append("\n");
            }

        }
        return sb.toString();
    }

    private Object getJson(Object source) {
        return new Gson().toJson(source);
    }

    @Override
    public Object getData() {
        Set<AbstractAction> set = new LinkedHashSet<AbstractAction>();
        set.addAll(indexSet);
        set.addAll(deleteSet);
        return prepareBulk(set);
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getName() {
        return "BULK";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];
    }
}
