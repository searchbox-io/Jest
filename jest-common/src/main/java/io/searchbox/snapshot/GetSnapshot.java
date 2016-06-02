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
		if (builder.isCurrent) {
			snapshotNamePath = "/_current";
		} else if (builder.isAll) {
			snapshotNamePath = "/_all";
		} else if (builder.getJoinedSnapshots().length() > 0) {
			snapshotNamePath = "/" + builder.getJoinedSnapshots();
		}
		setURI(buildURI() + "/" + builder.repository + snapshotNamePath);
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

		private Set<String> snapshots = new LinkedHashSet<String>();
		private String repository;
		private boolean isAll = false;
		private boolean isCurrent;

		public Builder(String repository) {
			this.repository = repository;
		}

		public Builder addSnapshot(Collection<? extends String> snapshots) {
			this.snapshots.addAll(snapshots);
			return this;
		}

		public Builder addSnapshot(String snapshot) {
			this.snapshots.add(snapshot);
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

		public String getJoinedSnapshots() {
			return StringUtils.join(snapshots, ",");
		}

		@Override
		public GetSnapshot build() {
			return new GetSnapshot(this);
		}
	}
}
