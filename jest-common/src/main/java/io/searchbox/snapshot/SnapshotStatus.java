package io.searchbox.snapshot;

import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class SnapshotStatus extends GenericResultAbstractAction {

	protected SnapshotStatus(Builder builder) {
		super(builder);
		setURI(buildURI() + "/" + builder.repository + "/" + builder.getJoinedSnapshots() + "/_status");
	}

	@Override
	protected String buildURI() {
		return "/_snapshot";
	}

	@Override
	public String getRestMethodName() {
		return "GET";
	}

	public static class Builder extends GenericResultAbstractAction.Builder<SnapshotStatus, Builder> {
		private String repository;
		private Set<String> snapshots = new LinkedHashSet<String>();

		public Builder(String repository) {
			this.repository = repository;
		}

		public Builder addSnapshot(Collection<? extends String> snapshots) {
			this.snapshots.addAll(snapshots);
			return this;
		}

		public String getJoinedSnapshots() {
			return StringUtils.join(snapshots, ",");
		}

		public Builder snapshot(String snapshot) {
			this.snapshots.add(snapshot);
			return this;
		}

		@Override
		public SnapshotStatus build() {
			return new SnapshotStatus(this);
		}
	}
}
