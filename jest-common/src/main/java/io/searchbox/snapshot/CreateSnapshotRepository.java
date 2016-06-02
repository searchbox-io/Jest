package io.searchbox.snapshot;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class CreateSnapshotRepository extends AbstractSnapshotRepositoryAction {

    protected CreateSnapshotRepository(Builder builder) {
        super(builder);

        this.payload = builder.settings;
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    public static class Builder extends AbstractSnapshotRepositoryAction.SingleRepositoryBuilder<CreateSnapshotRepository, Builder> {
        private Object settings;

        public Builder(String repository) {
            super(repository);
        }

        public Builder settings(Object settings) {
            this.settings = settings;
            return this;
        }

        public Builder verify(boolean verify) {
            return setParameter("verify", verify);
        }

        @Override
        public CreateSnapshotRepository build() {
            return new CreateSnapshotRepository(this);
        }
    }
}
