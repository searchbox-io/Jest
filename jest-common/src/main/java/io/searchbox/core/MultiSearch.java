package io.searchbox.core;

import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;
import io.searchbox.params.Parameters;
import io.searchbox.strings.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class MultiSearch extends AbstractAction<MultiSearchResult> {
    private static final CharMatcher NEWLINE_MATCHER = CharMatcher.anyOf("\r\n").precomputed();

    private Collection<Search> searches;

    protected MultiSearch(Builder builder) {
        super(builder);
        this.searches = builder.searchList;
    }

    @Override
    public MultiSearchResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new MultiSearchResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getData(Gson gson) {
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
            sb.append(getParameter(search, Parameters.IGNORE_UNAVAILABLE));
            sb.append(getParameter(search, Parameters.ALLOW_NO_INDICES));
            sb.append(getParameter(search, Parameters.EXPAND_WILDCARDS));
            sb.append(getParameter(search, Parameters.ROUTING));
            sb.append(getParameter(search, Parameters.SEARCH_TYPE));

            final String query = NEWLINE_MATCHER.removeFrom(search.getData(gson));
            sb.append("\"}\n")
                    .append(query)
                    .append("\n");
        }
        return sb.toString();
    }

    private String getParameter(Search search, String parameter) {
        final Collection<Object> searchParameter = search.getParameter(parameter);
        if (searchParameter != null) {
            final int parameters = searchParameter.size();
            if (parameters == 1) {
                return "\", \"" + parameter + "\" : \"" + searchParameter.iterator().next();
            } else if (parameters > 1) {
                throw new IllegalArgumentException("Expecting a single value for '" + parameter + "' parameter, you provided: " + parameters);
            }
        }

        return "";
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_msearch";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), searches);
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

        MultiSearch rhs = (MultiSearch) obj;
        return super.equals(obj) && Objects.equals(searches, rhs.searches);
    }

    public static class Builder extends GenericResultAbstractAction.Builder<MultiSearch, Builder> {
        private List<Search> searchList = new LinkedList<>();

        public Builder(Search search) {
            setHeader("Content-Type", "application/x-ndjson");
            searchList.add(search);
        }

        public Builder(Collection<? extends Search> searches) {
            setHeader("Content-Type", "application/x-ndjson");
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
