package io.searchbox.core;

import io.searchbox.AbstractAction;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Dogukan Sonmez
 */


public class MultiSearch extends AbstractAction {

    private final Set<Search> searchSet = new LinkedHashSet<Search>();

    public MultiSearch() {
        setURI(buildURI());
    }

    public void addSearch(Search search) {
        if (search != null) searchSet.add(search);
    }

    public boolean contains(Search search) {
        return searchSet.contains(search);
    }

    public void removeSearch(Search search) {
        searchSet.remove(search);
    }

    protected Object prepareBulk() {
        /*
            {"index" : "test"}
            {"query" : {"match_all" : {}}, "from" : 0, "size" : 10}
            {"index" : "test", "search_type" : "count"}
            {"query" : {"match_all" : {}}}
            {}
            {"query" : {"match_all" : {}}}
         */
        StringBuilder sb = new StringBuilder();
        for (Search search : searchSet) {
            sb.append("{\"index\" : \"").append(search.getIndex());
            if (StringUtils.isNotBlank(search.getType())) {
                sb.append("\", \"type\" : \"").append(search.getType());
            }
            sb.append("\"}\n{\"query\" : ")
                    .append(search.getData())
                    .append("}\n");
        }
        return sb.toString();
    }

    @Override
    public Object getData() {
        return prepareBulk();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI() {
        return "/_msearch";
    }
}
