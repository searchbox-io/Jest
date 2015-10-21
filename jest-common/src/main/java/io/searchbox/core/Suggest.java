package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Suggest extends AbstractAction<SuggestResult> {

    protected Suggest(final Builder builder) {
        super(builder);

        this.payload = builder.getQuery();
        this.setURI(this.buildURI());
    }

    @Override
    public SuggestResult createNewElasticSearchResult(final String json, final int statusCode, final String reasonPhrase, final Gson gson) {
        return this.createNewElasticSearchResult(new SuggestResult(gson), json, statusCode, reasonPhrase, gson);
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public String getIndex() {
        return this.indexName;
    }

    public String getType() {
        return this.typeName;
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_suggest";
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

    public static class Builder extends AbstractMultiTypeActionBuilder<Suggest, Builder> {

        private final String query;

        /**
         * Suggest requests executed against the _suggest endpoint should omit the
         * surrounding suggest element which is only used if the suggest request is part of a search.
         */
        public Builder(final String query) {
            this.query = query;
        }

        public final String getQuery() {
            return this.query;
        }

        @Override
        public Suggest build() {
            return new Suggest(this);
        }

    }

}
