package io.searchbox.core;

import io.searchbox.AbstractAction;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class MultiSearch extends AbstractAction {

    public MultiSearch(Builder builder) {
        super(builder);
        setURI(buildURI());
        setData(generateData(builder.searchList));
    }

    protected static Object generateData(Collection<? extends Search> searches) {
        /*
            {"index" : "test"}
            {"query" : {"match_all" : {}}, "from" : 0, "size" : 10}
            {"index" : "test", "search_type" : "count"}
            {"query" : {"match_all" : {}}}
            {}
            {"query" : {"match_all" : {}}}
         */
        StringBuilder sb = new StringBuilder();
        for (Search search : searches) {
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
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_msearch");
        return sb.toString();
    }

    public static class Builder extends AbstractAction.Builder<MultiSearch, Builder> {
        private List<Search> searchList = new LinkedList<Search>();

        public Builder(Search search) {
            searchList.add(search);
        }

        public Builder(Collection<? extends Search> searches) {
            searchList.addAll(searches);
        }

        public Builder addSearch(Search search) {
            searchList.add(search);
            return this;
        }

        public Builder addSearch(Collection<? extends Search> searches) {
            searchList.addAll(searches);
            return this;
        }

        @Override
        public MultiSearch build() {
            return new MultiSearch(this);
        }
    }
}
