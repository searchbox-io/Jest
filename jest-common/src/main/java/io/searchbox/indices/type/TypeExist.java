package io.searchbox.indices.type;

import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author happyprg(hongsgo @ gmail.com)
 */
public class TypeExist extends GenericResultAbstractAction {

    TypeExist(Builder builder) {
        super(builder);
    }

    @Override
    protected String getURLCommandExtension(ElasticsearchVersion elasticsearchVersion) {
        return elasticsearchVersion == ElasticsearchVersion.V55 ? "_mapping" : super.getURLCommandExtension(elasticsearchVersion);
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
