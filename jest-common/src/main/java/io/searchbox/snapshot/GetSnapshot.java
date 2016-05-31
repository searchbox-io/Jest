package io.searchbox.snapshot;

import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class GetSnapshot extends GenericResultAbstractAction {


	protected GetSnapshot(Builder builder) {
		super(builder);

		String snapshotNamePath = StringUtils.EMPTY;
		if(builder.isCurrent){
			snapshotNamePath = "/_current";
		}
		else if (builder.isAll) {
			snapshotNamePath = "/_all";
		} else if (builder.getJoinedSnapshotNames().length() > 0) {
			snapshotNamePath = "/" + builder.getJoinedSnapshotNames();
		}
		setURI(buildURI() + "/" + builder.repositoryName + snapshotNamePath);
	}

	@Override
	protected String buildURI() {
		return "/_snapshot";
	}

	@Override
	public String getRestMethodName() {
		return "GET";
	}

	public static class Builder extends GenericResultAbstractAction.Builder<GetSnapshot, Builder> {
		private String repositoryName;
		private Set<String> snapshotNames = new LinkedHashSet<String>();
		private boolean isAll = false;
		private boolean isCurrent;


		public Builder(String repositoryName) {
			this.repositoryName = repositoryName;
		}

		public Builder addSnapshotNames(Collection<? extends String> snapshotNames) {
			this.snapshotNames.addAll(snapshotNames);
			return this;
		}

		public Builder current(boolean current) {
			this.isCurrent = current;
			return this;
		}
		public Builder all(boolean all) {
			this.isAll = all;
			return this;
		}

		public String getJoinedSnapshotNames() {
			return StringUtils.join(snapshotNames, ",");
		}

		public Builder snapshotName(String snapshotName) {
			this.snapshotNames.add(snapshotName);
			return this;
		}

		@Override
		public GetSnapshot build() {
			return new GetSnapshot(this);
		}
	}
}
