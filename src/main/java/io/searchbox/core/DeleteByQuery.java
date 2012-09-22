package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class DeleteByQuery extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(DeleteByQuery.class.getName());

    final private LinkedHashSet<String> indexSet = new LinkedHashSet<String>();

    final private LinkedHashSet<String> typeSet = new LinkedHashSet<String>();

    public DeleteByQuery(String query) {
        setData(query);
    }

    protected DeleteByQuery() {
    }

    public void addIndex(String index) {
        if (StringUtils.isNotBlank(index)) {
            indexSet.add(index);
        }
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
        if (indexQuery.length() == 0) {
            sb.append("_all/");
        } else {
            sb.append(indexQuery).append("/");
            if (typeQuery.length() > 0) {
                sb.append(typeQuery).append("/");
            }
        }
        sb.append("_query");
        log.debug("Created URI for delete by query action is : " + sb.toString());
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
        return "DELETEBYQUERY";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }
}
