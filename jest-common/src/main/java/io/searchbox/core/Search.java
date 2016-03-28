package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Search extends AbstractAction<SearchResult> {

    private String query;
    private List<Sort> sortList = new LinkedList<Sort>();

    protected Search(Builder builder) {
        super(builder);

        this.query = builder.query;
        this.sortList = builder.sortList;
        setURI(buildURI());
    }
    
    protected Search(TemplateBuilder templatedBuilder) {
        super(templatedBuilder);

        //reuse query as it's just the request body of the POST
        this.query = templatedBuilder.query;
        this.sortList = templatedBuilder.sortList;
        setURI(buildURI() + "/template");
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
        return super.buildURI() + "/_search";
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
    public String getData(Gson gson) {
        String data;
        if (sortList.isEmpty()) {
            data = query;
        } else {
            Map<String, Object> rootJson = gson.fromJson(query, Map.class);

            List<Map<String, Object>> sortMaps = (List<Map<String, Object>>) rootJson.get("sort");
            if (sortMaps == null) {
                sortMaps = new ArrayList<Map<String, Object>>(sortList.size());
                rootJson.put("sort", sortMaps);
            }

            for (Sort sort : sortList) {
                sortMaps.add(sort.toMap());
            }

            data = gson.toJson(rootJson);
        }
        return data;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(query)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        Search rhs = (Search) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(query, rhs.query)
                .append(sortList, rhs.sortList)
                .isEquals();
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<Search, Builder> {
        protected String query;
        protected List<Sort> sortList = new LinkedList<Sort>();
        
        protected Builder() {
        	
        }

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
    
    public static class TemplateBuilder extends Builder {    	
    	public TemplateBuilder(String templatedQuery) {    		
            super.query = templatedQuery;
        }
    	
    	@Override
        public Search build() {
            return new Search(this);
        }
    }
}
