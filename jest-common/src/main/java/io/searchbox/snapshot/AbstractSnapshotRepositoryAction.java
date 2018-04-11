package io.searchbox.snapshot;

import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author ckeser
 */
public abstract class AbstractSnapshotRepositoryAction extends GenericResultAbstractAction {

    private String repositories;

    protected AbstractSnapshotRepositoryAction(RepositoryBuilder builder) {
        super(builder);

        this.repositories = builder.getRepositories();
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_snapshot/" + repositories;
    }

    public abstract static class RepositoryBuilder<T extends AbstractSnapshotRepositoryAction, K> extends Builder<T, K> {
        protected abstract String getRepositories();
    }

    public abstract static class SingleRepositoryBuilder<T extends AbstractSnapshotRepositoryAction, K> extends RepositoryBuilder<T, K> {
        private String repository;

        public SingleRepositoryBuilder(String repository) {
            this.repository = repository;
        }

        protected String getRepositories() {
            return repository;
        }
    }
}
