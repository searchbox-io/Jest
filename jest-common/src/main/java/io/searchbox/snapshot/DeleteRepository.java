package io.searchbox.snapshot;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class DeleteRepository extends GenericResultAbstractAction {


	protected DeleteRepository(Builder builder) {
		super(builder);
		setURI(buildURI() + "/" + builder.repositoryName);
	}

	@Override
	protected String buildURI() {
		return "/_snapshot";
	}

	@Override
	public String getRestMethodName() {
		return "DELETE";
	}

	public static class Builder extends GenericResultAbstractAction.Builder<DeleteRepository, Builder> {
		private String repositoryName;

		public Builder(String repositoryName) {
			this.repositoryName = repositoryName;
		}

		@Override
		public DeleteRepository build() {
			return new DeleteRepository(this);
		}
	}
}
