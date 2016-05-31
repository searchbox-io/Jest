package io.searchbox.snapshot;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class DeleteSnapshot extends GenericResultAbstractAction {


	protected DeleteSnapshot(Builder builder) {
		super(builder);
		setURI(buildURI() + "/" + builder.repositoryName + "/" + builder.snapshotName);
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
		private String repositoryName;
		private String snapshotName;

		public Builder(String repositoryName) {
			this.repositoryName = repositoryName;
		}

		public Builder snapshotName(String snapshotName) {
			this.snapshotName = snapshotName;
			return this;
		}

		@Override
		public DeleteSnapshot build() {
			return new DeleteSnapshot(this);
		}
	}
}
