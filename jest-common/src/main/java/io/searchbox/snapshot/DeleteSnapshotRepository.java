package io.searchbox.snapshot;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class DeleteSnapshotRepository extends AbstractSnapshotRepositoryAction {

    protected DeleteSnapshotRepository(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    public static class Builder extends AbstractSnapshotRepositoryAction.SingleRepositoryBuilder<DeleteSnapshotRepository, Builder> {
        public Builder(String repository) {
            super(repository);
        }

        @Override
        public DeleteSnapshotRepository build() {
            return new DeleteSnapshotRepository(this);
        }
    }
}
