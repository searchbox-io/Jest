package io.searchbox.indices.type;

import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class TypeExist extends GenericResultAbstractAction {

	private final boolean compatibleFor55;

	TypeExist(Builder builder, boolean compatibleFor55) {
		super(builder);
		this.compatibleFor55 = compatibleFor55;
		setURI(buildURI());
	}

	@Override
	protected String getURLCommandExtension() {
		return compatibleFor55 ? "_mapping" : super.getURLCommandExtension();
	}

	@Override
	public String getRestMethodName() {
		return "HEAD";
	}

	public static class Builder extends AbstractMultiTypeActionBuilder<TypeExist, Builder> {

		private boolean compatibleFor55 = false;

		public Builder(String index) {
			addIndex(index);
		}

		public Builder makeCompatibleForVersion55() {
			compatibleFor55 = true;
			return this;
		}

		@Override
		public TypeExist build() {
			return new TypeExist(this, compatibleFor55);
		}
	}

}
