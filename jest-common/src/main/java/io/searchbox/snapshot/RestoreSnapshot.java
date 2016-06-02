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

		setURI(buildURI() + "/" + builder.repository + "/" + builder.snapshot + "/_restore");
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
		private String repository;
		private String snapshot;
		private Object settings;

		public Builder(String repository) {
			this.repository = repository;
		}

		public Builder snapshotName(String snapshot) {
			this.snapshot = snapshot;
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
