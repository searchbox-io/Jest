package io.searchbox.snapshot;

import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public abstract class AbstractSnapshotAction extends GenericResultAbstractAction {
	protected Set<String> repositoryNames = new LinkedHashSet<String>();
	protected String repositoryName;

	protected AbstractSnapshotAction(Builder builder) {
		super(builder);
		repositoryName = builder.repositoryName;
	}

	@Override
	protected String buildURI() {
		StringBuilder sb = new StringBuilder("_snapshot");
		if (StringUtils.isNotBlank(repositoryName)) {
			sb.append("/" + repositoryName);
		}

		if (repositoryNames.size() > 0) {
			sb.append(StringUtils.join(repositoryNames, ","));
		}

		return sb.toString();
	}

	protected abstract static class Builder<T extends AbstractSnapshotAction, K> extends GenericResultAbstractAction.Builder<T, K> {
		protected String repositoryName;
		protected Set<String> repositoryNames;

		public Builder() {
		}

		public Builder(String template) {
			this.repositoryName = template;
		}

		public Builder addRepositoryNames(Set<String> repositoryNames) {
			this.repositoryNames = repositoryNames;
			return this;
		}
	}
}
