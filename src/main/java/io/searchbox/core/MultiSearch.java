package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Dogukan Sonmez
 */


public class MultiSearch extends AbstractAction implements Action {

    private final Set<Search> searchSet = new LinkedHashSet<Search>();

    public void addSearch(Search search) {
        if (search != null) searchSet.add(search);
    }

    public boolean isSearchExist(Search search) {
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
            if (search.indexSet.size() > 0) {
                for (String index : search.indexSet) {
                    sb.append("{\"index\" : \"").append(index).append("\"}\n");
                    sb.append("{\"query\" : ").append(search.getData()).append("}\n");
                }
            } else {
                sb.append("{}\n");
                sb.append("{\"query\" : ").append(search.getData()).append("}\n");
            }
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
    public String getName() {
        return "MULTISEARCH";
    }

    @Override
    public String getURI() {
        return "/_msearch";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
