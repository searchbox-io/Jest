package io.searchbox.snapshot;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class CreateSnapshot extends GenericResultAbstractAction {


	protected CreateSnapshot(Builder builder) {
		super(builder);

		if (builder.settings != null) {
			this.payload = builder.settings;
		} else {
			this.payload = new Object();
		}
		setURI(buildURI() + "/" + builder.repositoryName + "/" + builder.snapshotName);
	}

	@Override
	protected String buildURI() {
		return "/_snapshot";
	}

	@Override
	public String getRestMethodName() {
		return "PUT";
	}

	public static class Builder extends GenericResultAbstractAction.Builder<CreateSnapshot, Builder> {

		private Object settings;
		private String repositoryName;
		private String snapshotName;

		public Builder settings(Object settings) {
			this.settings = settings;
			return this;
		}
		
		public Builder(String repositoryName) {
			this.repositoryName = repositoryName;
		}

		public Builder snapshotName(String snapshotName) {
			this.snapshotName = snapshotName;
			return this;
		}

		public Builder waitForCompletion(boolean waitForCompletion) {
			return setParameter("wait_for_completion", waitForCompletion);
		}

		@Override
		public CreateSnapshot build() {
			return new CreateSnapshot(this);
		}
	}
}
