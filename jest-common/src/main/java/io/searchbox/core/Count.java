package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Count extends AbstractAction<CountResult> {

    protected Count(Builder builder) {
        super(builder);
        this.payload = builder.query;
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_count";
    }

    @Override
    public String getPathToResult() {
        return "count";
    }

    @Override
    public CountResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new CountResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<Count, Builder> {
        private String query;

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        @Override
        public Count build() {
            return new Count(this);
        }
    }
}
