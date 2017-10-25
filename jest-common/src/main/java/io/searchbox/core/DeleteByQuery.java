package io.searchbox.core;

import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * Delete By Query API  is removed in Elasticsearch version 2.0.
 * You need to install the plugin with the same name for this action to work.
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 * @see <a href="https://www.elastic.co/blog/core-delete-by-query-is-a-plugin">Delete By Query is now a plugin</a>
 */
public class DeleteByQuery extends GenericResultAbstractAction {

    protected DeleteByQuery(Builder builder) {
        super(builder);

        this.payload = builder.query;
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_delete_by_query";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<DeleteByQuery, Builder> {

        private String query;

        public Builder(String query) {
            this.query = query;
        }

        @Override
        public DeleteByQuery build() {
            return new DeleteByQuery(this);
        }
    }

}
