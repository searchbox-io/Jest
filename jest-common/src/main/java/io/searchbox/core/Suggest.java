package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.client.config.ElasticsearchVersion;

public class Suggest extends AbstractAction<SuggestResult> {

    protected Suggest(final Builder builder) {
        super(builder);
        this.payload = builder.getQuery();
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
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_suggest";
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
