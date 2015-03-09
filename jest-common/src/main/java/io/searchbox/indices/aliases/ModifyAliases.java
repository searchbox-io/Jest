package io.searchbox.indices.aliases;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

/**
 * @author cihat keser
 */
public class ModifyAliases extends GenericResultAbstractAction {

    private Map<String, Object> data;

    private ModifyAliases(Builder builder) {
        super(builder);

        List<Map> actions = new LinkedList<Map>();
        for (AliasMapping aliasMapping : builder.actions) {
            actions.addAll(aliasMapping.getData());
        }
        this.data = ImmutableMap.<String, Object>of("actions", actions);
        setURI(buildURI());
    }

    @Override
    public Object getData(Gson gson) {
        return data;
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(data)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        ModifyAliases rhs = (ModifyAliases) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(data, rhs.data)
                .isEquals();
    }

    public static class Builder extends GenericResultAbstractAction.Builder<ModifyAliases, Builder> {
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
