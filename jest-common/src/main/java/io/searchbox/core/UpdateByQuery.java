package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Lior Knaany
 */
public class UpdateByQuery extends AbstractAction<UpdateByQueryResult> {

    protected UpdateByQuery(Builder builder) {
        super(builder);

        this.payload = builder.payload;
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_update_by_query";
    }

    @Override
    public String getPathToResult() {
        return null;
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
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

        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .isEquals();
    }

    @Override
    public UpdateByQueryResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new UpdateByQueryResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<UpdateByQuery, Builder> {

        private Object payload;

        public Builder(Object payload) {this.payload = payload;}

        @Override
        public UpdateByQuery build() {
            return new UpdateByQuery(this);
        }
    }

}
