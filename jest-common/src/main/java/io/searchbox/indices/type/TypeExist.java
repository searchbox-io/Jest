package io.searchbox.indices.type;

import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class TypeExist extends GenericResultAbstractAction {

	protected TypeExist(Builder builder) {
		super(builder);
		setURI(buildURI());
	}

	@Override
	public String getRestMethodName() {
		return "HEAD";
	}

	public static class Builder extends AbstractMultiTypeActionBuilder<TypeExist, Builder> {

		public Builder(String index) {
			addIndex(index);
		}

		@Override
		public TypeExist build() {
			return new TypeExist(this);
		}
	}

}
