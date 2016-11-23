package io.searchbox.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Search extends AbstractAction<SearchResult> {

    private String query;
    private List<Sort> sortList = new LinkedList<Sort>();
    protected List<String> includePatternList = new ArrayList<String>();
    protected List<String> excludePatternList = new ArrayList<String>();

    protected Search(Builder builder) {
        super(builder);

        this.query = builder.query;
        this.sortList = builder.sortList;
        this.includePatternList = builder.includePatternList;
        this.excludePatternList = builder.excludePatternList;
        setURI(buildURI());
    }

    protected Search(TemplateBuilder templatedBuilder) {
        super(templatedBuilder);

        //reuse query as it's just the request body of the POST
        this.query = templatedBuilder.query;
        this.sortList = templatedBuilder.sortList;
        this.includePatternList = templatedBuilder.includePatternList;
        this.excludePatternList = templatedBuilder.excludePatternList;
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

    @Override
    public String getData(Gson gson) {
        String data;
        if (sortList.isEmpty() && includePatternList.isEmpty() && excludePatternList.isEmpty()) {
            data = query;
        } else {
            JsonObject queryObject = gson.fromJson(query, JsonObject.class);

            if (queryObject == null) {
                queryObject = new JsonObject();
            }

            JsonArray sortArray;
            if (queryObject.has( "sort" )) {
                sortArray = queryObject.get("sort").getAsJsonArray();
            } else {
                sortArray = new JsonArray();
                queryObject.add("sort", sortArray);
            }

            for (Sort sort : sortList) {
                sortArray.add(sort.toJsonObject());
            }

            if (!includePatternList.isEmpty() || !excludePatternList.isEmpty()) {
                JsonObject sourceObject;
                if (queryObject.has("_source")) {
                    sourceObject = queryObject.get("_source").getAsJsonObject();
                } else {
                    sourceObject = new JsonObject();
                    queryObject.add("_source", sourceObject);
                }

                addPatternListToSource(sourceObject, "include", includePatternList);
                addPatternListToSource(sourceObject, "exclude", excludePatternList);
            }

            data = gson.toJson(queryObject);
        }
        return data;
    }

    private static void addPatternListToSource(JsonObject sourceObject, String rule, List<String> patternList) {
        if (!patternList.isEmpty()) {
            JsonArray ruleArray;
            if (sourceObject.has(rule)) {
                ruleArray = sourceObject.get(rule).getAsJsonArray();
            } else {
                ruleArray = new JsonArray();
                sourceObject.add(rule, ruleArray);
            }
            for (String pattern : patternList) {
                ruleArray.add(pattern);
            }
        }
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
                .append(includePatternList, rhs.includePatternList)
                .append(excludePatternList, rhs.excludePatternList)
                .isEquals();
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<Search, Builder> {
        protected String query;
        protected List<Sort> sortList = new LinkedList<Sort>();
        protected List<String> includePatternList = new ArrayList<String>();
        protected List<String> excludePatternList = new ArrayList<String>();

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

        public Builder addSourceExcludePattern(String excludePattern) {
            excludePatternList.add(excludePattern);
            return this;
        }

        public Builder addSourceIncludePattern(String includePattern) {
            includePatternList.add(includePattern);
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

    public static class VersionBuilder extends Builder {
        public VersionBuilder(String query) {
            super(query);
            this.setParameter(Parameters.VERSION, "true");
        }
    }

    public static class TemplateBuilder extends Builder {
    	public TemplateBuilder(String templatedQuery) {
            super(templatedQuery);
        }

    	@Override
        public Search build() {
            return new Search(this);
        }
    }
}
