package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.client.config.ElasticsearchVersion;

import java.util.Objects;

/**
 * @author Lior Knaany
 */
public class UpdateByQuery extends AbstractAction<UpdateByQueryResult> {

    protected UpdateByQuery(Builder builder) {
        super(builder);
        this.payload = builder.payload;
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_update_by_query";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
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

        return super.equals(obj);
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
