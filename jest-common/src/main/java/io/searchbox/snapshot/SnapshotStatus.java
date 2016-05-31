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
		setURI(buildURI() + "/" + builder.repositoryName + "/" + builder.getJoinedSnapshotNames() + "/_status");
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
		private String repositoryName;
		private Set<String> snapshotNames = new LinkedHashSet<String>();

		public Builder(String repositoryName) {
			this.repositoryName = repositoryName;
		}

		public Builder addSnapshotNames(Collection<? extends String> snapshotNames) {
			this.snapshotNames.addAll(snapshotNames);
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
		public SnapshotStatus build() {
			return new SnapshotStatus(this);
		}
	}
}
