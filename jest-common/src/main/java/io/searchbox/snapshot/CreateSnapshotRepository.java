package io.searchbox.snapshot;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class CreateSnapshotRepository extends GenericResultAbstractAction {


	protected CreateSnapshotRepository(Builder builder) {

		super(builder);

		if (builder.settings != null) {
			this.payload = builder.settings;
		} else {
			this.payload = new Object();
		}
		setURI(buildURI() + "/" + builder.repositoryName);
	}

	@Override
	protected String buildURI() {
		return "/_snapshot";
	}

	@Override
	public String getRestMethodName() {
		return "PUT";
	}

	public static class Builder extends GenericResultAbstractAction.Builder<CreateSnapshotRepository, Builder> {
		private String repositoryName;
		private Object settings;

		public Builder(String repositoryName) {
			this.repositoryName = repositoryName;
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
