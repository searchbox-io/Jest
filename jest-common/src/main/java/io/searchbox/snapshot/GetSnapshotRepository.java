package io.searchbox.snapshot;

import com.google.common.base.Joiner;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class GetSnapshotRepository extends AbstractSnapshotRepositoryAction {

    protected GetSnapshotRepository(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractSnapshotRepositoryAction.RepositoryBuilder<GetSnapshotRepository, Builder> {
        private Set<String> repositories = new LinkedHashSet<String>();

        public Builder() {
        }

        public Builder(String repository) {
            this.repositories.add(repository);
        }

        public Builder(Collection<? extends String> repositories) {
            this.repositories.addAll(repositories);
        }

        public Builder addRepository(Collection<? extends String> repositories) {
            this.repositories.addAll(repositories);
            return this;
        }

        @Override
        public GetSnapshotRepository build() {
            return new GetSnapshotRepository(this);
        }

        @Override
        protected String getRepositories() {
            if (repositories.isEmpty()) {
                return "_all";
            } else {
                return Joiner.on(',').join(repositories);
            }
        }
    }
}
