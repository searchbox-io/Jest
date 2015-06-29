package io.searchbox.indices.aliases;

import java.util.List;

/**
 * @author cihat keser
 */
public class RemoveAliasMapping extends AliasMapping {

    protected RemoveAliasMapping() {
    }

    protected RemoveAliasMapping(Builder builder) {
        this.indices.addAll(builder.indices);
        this.alias = builder.alias;
        this.filter = builder.filter;
        this.searchRouting.addAll(builder.searchRouting);
        this.indexRouting.addAll(builder.indexRouting);
    }

    @Override
    public String getType() {
        return "remove";
    }

    public static class Builder extends AbstractAliasMappingBuilder<RemoveAliasMapping, Builder> {

        public Builder(List<String> indices, String alias) {
            super(indices, alias);
        }

        public Builder(String index, String alias) {
            super(index, alias);
        }

        @Override
        public RemoveAliasMapping build() {
            return new RemoveAliasMapping(this);
        }
    }
}
