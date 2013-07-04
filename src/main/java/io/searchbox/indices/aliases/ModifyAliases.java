package io.searchbox.indices.aliases;

import io.searchbox.AbstractAction;

import java.util.*;

/**
 * @author cihat keser
 */
public class ModifyAliases extends AbstractAction {

    private ModifyAliases(Builder builder) {
        super(builder);
        List<Map> actions = new LinkedList<Map>();
        for (AliasMapping aliasMapping : builder.actions) {
            actions.addAll(aliasMapping.getData());
        }

        Map<String, Object> data = new HashMap<String, Object>(1);
        data.put("actions", actions);

        setData(data);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_aliases");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends AbstractAction.Builder<ModifyAliases, Builder> {
        private List<AliasMapping> actions = new LinkedList<AliasMapping>();

        public Builder(AliasMapping action) {
            actions.add(action);
        }

        public Builder(Collection<AliasMapping> actions) {
            this.actions.addAll(actions);
        }

        public Builder addAlias(AliasMapping action) {
            actions.add(action);
            return this;
        }

        public Builder addAlias(Collection<AliasMapping> actions) {
            this.actions.addAll(actions);
            return this;
        }

        public ModifyAliases build() {
            return new ModifyAliases(this);
        }
    }

}
