package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

/**
 * @author Dogukan Sonmez
 */

public class Bulk extends AbstractAction implements Action {

    private final Set<Index> indexSet = new LinkedHashSet<Index>();

    private final Set<Delete> deleteSet = new LinkedHashSet<Delete>();

    public Bulk() {
        setURI("_bulk");
    }

    public Bulk(String indexName) {
        setURI(buildURI(indexName, null, null) + "/_bulk");
    }

    public Bulk(String indexName, String typeName) {
        setURI(buildURI(indexName, typeName, null) + "/_bulk");
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

    public void addIndexList(Collection<?> sources) {
        for (Object source : sources) {
            addIndex(new Index.Builder(source).build());
        }
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

            boolean putComma = false;

            if (StringUtils.isNotBlank(action.getIndexName())) {
                sb.append("\"_index\" : \"");
                sb.append(action.getIndexName());
                sb.append("\"");

                putComma = true;
            }

            if (StringUtils.isNotBlank(action.getTypeName())) {
                if (putComma) sb.append(", ");
                sb.append("\"_type\" : \"");
                sb.append(action.getTypeName());
                sb.append("\"");

                putComma = true;
            }

            if (StringUtils.isNotBlank(action.getId())) {
                if (putComma) sb.append(", ");
                sb.append("\"_id\" : \"")
                        .append(action.getId());
                sb.append("\"");
            }

            sb.append("}}\n");
            if (action.getName().equalsIgnoreCase("index")) {
                sb.append(getJson(action.getData()));
                sb.append("\n");
            }

        }
        return sb.toString();
    }

    private Object getJson(Object source) {
        if (source instanceof String) {
            return source;
        } else {
            return new Gson().toJson(source);
        }
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
    public String getPathToResult() {
        return "ok";
    }
}
