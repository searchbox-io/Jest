package io.searchbox.core;


import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Search extends AbstractAction<SearchResult> {

    final static Logger log = LoggerFactory.getLogger(Search.class);
    private String query;
    private List<Sort> sortList = new LinkedList<Sort>();

    private Search(Builder builder) {
        super(builder);

        this.query = builder.query;
        this.sortList = builder.sortList;
        setURI(buildURI());
    }

    @Override
    public SearchResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new SearchResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }

    public String getIndex() {
        return this.indexName;
    }

    public String getType() {
        return this.typeName;
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_search");
        return sb.toString();
    }

    @Override
    public String getPathToResult() {
        return "hits/hits/_source";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getData(Gson gson) {
        String data;
        if (sortList.isEmpty()) {
            data = query;
        } else {
            List<Map<String, Object>> sortMaps = new ArrayList<Map<String, Object>>(sortList.size());
            for (Sort sort : sortList) {
                sortMaps.add(sort.toMap());
            }

            Map rootJson = gson.fromJson(query, Map.class);
            rootJson.put("sort", sortMaps);
            data = gson.toJson(rootJson);
        }
        return data;
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<Search, Builder> {
        private String query;
        private List<Sort> sortList = new LinkedList<Sort>();

        public Builder(String query) {
            this.query = query;
        }

        public Builder setSearchType(SearchType searchType) {
            return setParameter(Parameters.SEARCH_TYPE, searchType);
        }

        public Builder addSort(Sort sort) {
            sortList.add(sort);
            return this;
        }

        public Builder addSort(Collection<Sort> sorts) {
            sortList.addAll(sorts);
            return this;
        }

        @Override
        public Search build() {
            return new Search(this);
        }
    }
}
