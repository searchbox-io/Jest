package io.searchbox.snapshot;

import com.google.common.base.Joiner;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author ckeser
 */
public abstract class AbstractSnapshotAction extends GenericResultAbstractAction {

    private String repository;
    private String snapshots;

    protected AbstractSnapshotAction(SnapshotBuilder builder) {
        super(builder);

        this.repository = builder.repository;
        this.snapshots = builder.getSnapshots();
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_snapshot/" + repository + "/" + snapshots;
    }

    public abstract static class SnapshotBuilder<T extends AbstractSnapshotAction, K> extends Builder<T, K> {
        protected String repository;

        protected SnapshotBuilder(String repository) {
            this.repository = repository;
        }

        protected abstract String getSnapshots();
    }

    public abstract static class SingleSnapshotBuilder<T extends AbstractSnapshotAction, K> extends SnapshotBuilder<T, K> {
        private String snapshot;

        public SingleSnapshotBuilder(String repository, String snapshot) {
            super(repository);
            this.snapshot = snapshot;
        }

        @Override
        protected String getSnapshots() {
            return snapshot;
        }
    }

    @SuppressWarnings("unchecked")
    public abstract static class MultipleSnapshotBuilder<T extends AbstractSnapshotAction, K> extends SnapshotBuilder<T, K> {
        private Set<String> snapshots = new LinkedHashSet<String>();

        public MultipleSnapshotBuilder(String repository) {
            super(repository);
        }

        public K addSnapshot(Collection<? extends String> snapshots) {
            this.snapshots.addAll(snapshots);
            return (K) this;
        }

        public K addSnapshot(String snapshot) {
            this.snapshots.add(snapshot);
            return (K) this;
        }

        @Override
        protected String getSnapshots() {
            if (snapshots.isEmpty()) {
                return "_all";
            } else {
                return Joiner.on(',').join(snapshots);
            }
        }
    }
}
