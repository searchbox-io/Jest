package io.searchbox.snapshot;

import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class GetSnapshotRepository extends GenericResultAbstractAction {

	protected GetSnapshotRepository(Builder builder) {
		super(builder);

		String repositoryNamePath;
		if (builder.isAll) {
			repositoryNamePath = "_all";
		} else {
			repositoryNamePath = builder.getJoinedRepositories();
		}
		setURI(buildURI() + "/" + repositoryNamePath);
	}

	@Override
	protected String buildURI() {
		return "/_snapshot";
	}

	@Override
	public String getRestMethodName() {
		return "GET";
	}

	public static class Builder extends GenericResultAbstractAction.Builder<GetSnapshotRepository, Builder> {

		private Set<String> repositories = new LinkedHashSet<String>();
		private boolean isAll = false;

		public Builder(String repository) {
			this.repositories.add(repository);
		}

		public Builder addRepository(Collection<? extends String> repositories) {
			this.repositories.addAll(repositories);
			return this;
		}

		public String getJoinedRepositories() {
			return StringUtils.join(repositories, ",");
		}

		public Builder all(boolean all) {
			this.isAll = all;
			return this;
		}

		@Override
		public GetSnapshotRepository build() {
			return new GetSnapshotRepository(this);
		}
	}
}
