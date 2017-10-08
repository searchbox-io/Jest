package io.searchbox.indices.aliases;

import java.util.List;

/**
 * @author cihat keser
 */
public class AddAliasMapping extends AliasMapping {

    protected AddAliasMapping() {
    }

    protected AddAliasMapping(Builder builder) {
        this.indices.addAll(builder.indices);
        this.alias = builder.alias;
        this.filter = builder.filter;
        this.searchRouting.addAll(builder.searchRouting);
        this.indexRouting.addAll(builder.indexRouting);
    }

    @Override
    public String getType() {
        return "add";
    }

    public static class Builder extends AbstractAliasMappingBuilder<AddAliasMapping, Builder> {

        public Builder(List<String> indices, String alias) {
            super(indices, alias);
        }

        public Builder(String index, String alias) {
            super(index, alias);
        }

        @Override
        public AddAliasMapping build() {
            return new AddAliasMapping(this);
        }
    }
}
