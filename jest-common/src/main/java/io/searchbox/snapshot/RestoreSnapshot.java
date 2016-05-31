package io.searchbox.snapshot;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class RestoreSnapshot extends GenericResultAbstractAction {


	protected RestoreSnapshot(Builder builder) {
		super(builder);

		if (builder.settings != null) {
			this.payload = builder.settings;
		} else {
			this.payload = new Object();
		}

		setURI(buildURI() + "/" + builder.repositoryName + "/" + builder.snapshotName + "/_restore");
	}

	@Override
	protected String buildURI() {
		return "/_snapshot";
	}

	@Override
	public String getRestMethodName() {
		return "POST";
	}

	public static class Builder extends GenericResultAbstractAction.Builder<RestoreSnapshot, Builder> {
		private String repositoryName;
		private String snapshotName;
		private Object settings;

		public Builder(String repositoryName) {
			this.repositoryName = repositoryName;
		}

		public Builder snapshotName(String snapshotName) {
			this.snapshotName = snapshotName;
			return this;
		}

		public Builder settings(Object settings) {
			this.settings = settings;
			return this;
		}

		@Override
		public RestoreSnapshot build() {
			return new RestoreSnapshot(this);
		}
	}
}
