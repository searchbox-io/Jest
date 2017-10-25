package io.searchbox.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.client.config.ElasticsearchVersion;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Search extends AbstractAction<SearchResult> {

    private String query;
    private List<Sort> sortList;
    protected List<String> includePatternList;
    protected List<String> excludePatternList;
    private final String templateSuffix;

    protected Search(Builder builder) {
        this(builder, "");
    }

    protected Search(TemplateBuilder templatedBuilder) {
        this(templatedBuilder, "/template");
    }

    private Search(Builder builder, String templateSuffix) {
        super(builder);
        this.query = builder.query;
        this.sortList = builder.sortList;
        this.includePatternList = builder.includePatternList;
        this.excludePatternList = builder.excludePatternList;
        this.templateSuffix = templateSuffix;
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
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_search" + templateSuffix;
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

            if (!sortList.isEmpty()) {
                JsonArray sortArray = normalizeSortClause(queryObject);

                for (Sort sort : sortList) {
                    sortArray.add(sort.toJsonObject());
                }
            }

            if (!includePatternList.isEmpty() || !excludePatternList.isEmpty()) {
                JsonObject sourceObject = normalizeSourceClause(queryObject);

                addPatternListToSource(sourceObject, "includes", includePatternList);
                addPatternListToSource(sourceObject, "excludes", excludePatternList);
            }

            data = gson.toJson(queryObject);
        }
        return data;
    }

    private static JsonArray normalizeSortClause(JsonObject queryObject) {
        JsonArray sortArray;
        if (queryObject.has("sort")) {
            JsonElement sortElement = queryObject.get("sort");
            if (sortElement.isJsonArray()) {
                sortArray = sortElement.getAsJsonArray();
            } else if (sortElement.isJsonObject()) {
                sortArray = new JsonArray();
                sortArray.add(sortElement.getAsJsonObject());
            } else if (sortElement.isJsonPrimitive() && sortElement.getAsJsonPrimitive().isString()) {
                String sortField = sortElement.getAsString();
                sortArray = new JsonArray();
                queryObject.add("sort", sortArray);
                String order;
                if ("_score".equals(sortField)) {
                    order = "desc";
                } else {
                    order = "asc";
                }
                JsonObject sortOrder = new JsonObject();
                sortOrder.add("order", new JsonPrimitive(order));
                JsonObject sortDefinition = new JsonObject();
                sortDefinition.add(sortField, sortOrder);

                sortArray.add(sortDefinition);
            } else {
                throw new JsonSyntaxException("_source must be an array, an object or a string");
            }
        } else {
            sortArray = new JsonArray();
        }
        queryObject.add("sort", sortArray);

        return sortArray;
    }

    private static JsonObject normalizeSourceClause(JsonObject queryObject) {
        JsonObject sourceObject;
        if (queryObject.has("_source")) {
            JsonElement sourceElement = queryObject.get("_source");

            if (sourceElement.isJsonObject()) {
                sourceObject = sourceElement.getAsJsonObject();
            } else if (sourceElement.isJsonArray()) {
                // in this case, the values of the array are includes
                sourceObject = new JsonObject();
                queryObject.add("_source", sourceObject);
                sourceObject.add("includes", sourceElement.getAsJsonArray());
            } else if (sourceElement.isJsonPrimitive() && sourceElement.getAsJsonPrimitive().isBoolean()) {
                // if _source is a boolean, we override the configuration with include/exclude
                sourceObject = new JsonObject();
            } else {
                throw new JsonSyntaxException("_source must be an object, an array or a boolean");
            }
        } else {
            sourceObject = new JsonObject();
        }
        queryObject.add("_source", sourceObject);

        return sourceObject;
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
        return Objects.hash(super.hashCode(), query, sortList, includePatternList, excludePatternList);
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
        return super.equals(obj)
                && Objects.equals(query, rhs.query)
                && Objects.equals(sortList, rhs.sortList)
                && Objects.equals(includePatternList, rhs.includePatternList)
                && Objects.equals(excludePatternList, rhs.excludePatternList);
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

        public Builder enableTrackScores() {
            this.setParameter(Parameters.TRACK_SCORES, true);
            return this;
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
