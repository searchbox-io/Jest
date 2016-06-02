package io.searchbox.snapshot;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class DeleteSnapshot extends AbstractSnapshotAction {

    protected DeleteSnapshot(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    public static class Builder extends AbstractSnapshotAction.SingleSnapshotBuilder<DeleteSnapshot, Builder> {
        public Builder(String repository, String snapshot) {
            super(repository, snapshot);
        }

        @Override
        public DeleteSnapshot build() {
            return new DeleteSnapshot(this);
        }
    }
}
