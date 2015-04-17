package io.searchbox.core;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.client.JestResult;

import com.google.gson.Gson;

public class Suggest extends AbstractAction<SuggestResult> {

    private final String query;

    private Suggest(final Builder builder) {
        super(builder);

        this.query = builder.getQuery();
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
    public Object getData(final Gson gson) {
        return this.query;
    }

    @Override
    protected String buildURI() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_suggest");
        return sb.toString();
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
