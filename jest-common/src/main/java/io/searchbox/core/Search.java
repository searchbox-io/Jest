package io.searchbox.core;


import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Search extends AbstractAction<SearchResult> {

    final static Logger log = LoggerFactory.getLogger(Search.class);
    private String query;
    private List<Sort> sortList = new LinkedList<Sort>();
    private int size = 10;
    private int from = 0;
    private double minScore=0D;
    private boolean explain=false;

    private Search(Builder builder) {
        super(builder);

        this.query = builder.query;
        this.sortList = builder.sortList;
        this.size = builder.size;
        this.from = builder.from;
        this.explain = builder.explain;
        this.minScore = builder.minScore;
        setURI(buildURI());
    }

    @Override
    public SearchResult createNewElasticSearchResult(String json, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new SearchResult(gson), json, statusCode, reasonPhrase, gson);
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

    @Override
    public Object getData(Gson gson) {
        String data;

        query = query.replaceFirst("\\{", "\\{ \"from\" : " + from +", \"size\" : " + size +", \"min_score\" : " + minScore + ", \"explain\" : "+ explain+ ", " );

        if (sortList.size() > 0) {
            StringBuilder sorting = new StringBuilder("\"sort\": [");
            sorting.append(StringUtils.join(sortList, ","));
            sorting.append("],");

            data = query.replaceFirst("\\{", "\\{" + sorting.toString());
        } else {
            data = query;
        }
        return data;
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<Search, Builder> {
        private String query;
        private List<Sort> sortList = new LinkedList<Sort>();
        private int size = 10;
        private int from = 0;
        private double minScore=0D;
        private boolean explain=false;

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

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder from(int from) {
            this.from = from;
            return this;
        }

        public Builder explain(boolean explain) {
            this.explain = explain;
            return this;
        }

        public Builder minScore (double minScore) {
            this.minScore = minScore;
            return this;
        }

        @Override
        public Search build() {
            return new Search(this);
        }
    }
}
