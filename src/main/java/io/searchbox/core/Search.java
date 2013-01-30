package io.searchbox.core;


import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author Dogukan Sonmez
 */


public class Search extends AbstractAction implements Action {

    final static Logger log = LoggerFactory.getLogger(Search.class);

    final protected LinkedHashSet<String> indexSet = new LinkedHashSet<String>();

    final protected LinkedHashSet<String> typeSet = new LinkedHashSet<String>();

    public Search(String query) {
        setData(query);
    }

    public Search(String query, List<Sort> sortList) {
        String sorting = "";
        for (Sort s : sortList) {
            if (s != sortList.get(0))
                sorting += ",\n";
            sorting += s.toString();
        }
        if (sorting.length() > 0)
            sorting = "\"sort\": [" + sorting + "], \n";
        setData("{\n" + sorting + query + "\n}");
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
        String queryString = buildQueryString();
        if (StringUtils.isNotBlank(queryString)) sb.append(queryString);

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

    @Override
    public String getPathToResult() {
        return "hits/hits/_source";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static String createQueryWithBuilder(String queryBuilderValue) {
        return "{\"query\":" + queryBuilderValue + "}";
    }
}
