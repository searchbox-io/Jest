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

		String repositoryNamePath=StringUtils.EMPTY;
		if (builder.isAll) {
			repositoryNamePath = "_all";
		} else if (builder.getJoinedRepositoryNames().length() > 0) {
			repositoryNamePath = builder.getJoinedRepositoryNames();
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
		private Set<String> repositoryNames = new LinkedHashSet<String>();
		private boolean isAll = false;

		public Builder(String repositoryName) {
			this.repositoryNames.add(repositoryName);
		}

		public Builder addRepositoryNames(Collection<? extends String> snapshotNames) {
			this.repositoryNames.addAll(snapshotNames);
			return this;
		}

		public String getJoinedRepositoryNames() {
			return StringUtils.join(repositoryNames, ",");
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
