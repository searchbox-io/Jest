package jp.naver.line.music.support.elasticsearch;

import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class TypeExist extends GenericResultAbstractAction {

	public TypeExist(Builder builder) {
		super(builder);
		setURI(buildURI());
	}

	@Override
	public String getRestMethodName() {
		return "HEAD";
	}

	public static class Builder extends AbstractMultiTypeActionBuilder<TypeExist, Builder> {

		@Override
		public TypeExist build() {
			return new TypeExist(this);
		}
	}

}
