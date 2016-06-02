package io.searchbox.snapshot;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class GetSnapshot extends AbstractSnapshotAction {

    protected GetSnapshot(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractSnapshotAction.MultipleSnapshotBuilder<GetSnapshot, Builder> {

        public Builder(String repository) {
            super(repository);
        }

        @Override
        public GetSnapshot build() {
            return new GetSnapshot(this);
        }
    }
}
