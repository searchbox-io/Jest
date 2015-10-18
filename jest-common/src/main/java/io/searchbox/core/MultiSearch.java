package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class MultiSearch extends AbstractAction<MultiSearchResult> {

    private Collection<Search> searches;

    protected MultiSearch(Builder builder) {
        super(builder);

        this.searches = builder.searchList;
        setURI(buildURI());
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
            sb.append("\"}\n")
                    .append(search.getData(gson))
                    .append("\n");
        }
        return sb.toString();
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_msearch";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(searches)
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

        MultiSearch rhs = (MultiSearch) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(searches, rhs.searches)
                .isEquals();
    }

    public static class Builder extends GenericResultAbstractAction.Builder<MultiSearch, Builder> {
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
