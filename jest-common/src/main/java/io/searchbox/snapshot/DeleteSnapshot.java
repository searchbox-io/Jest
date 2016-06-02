package io.searchbox.snapshot;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class DeleteSnapshot extends GenericResultAbstractAction {

	protected DeleteSnapshot(Builder builder) {
		super(builder);
		setURI(buildURI() + "/" + builder.repository + "/" + builder.snapshot);
	}

	@Override
	protected String buildURI() {
		return "/_snapshot";
	}

	@Override
	public String getRestMethodName() {
		return "DELETE";
	}

	public static class Builder extends GenericResultAbstractAction.Builder<DeleteSnapshot, Builder> {

		private String repository;
		private String snapshot;

		public Builder(String repository) {
			this.repository = repository;
		}

		public Builder snapshot(String snapshot) {
			this.snapshot = snapshot;
			return this;
		}

		@Override
		public DeleteSnapshot build() {
			return new DeleteSnapshot(this);
		}
	}
}
