package io.searchbox.indices.aliases;

import com.google.common.collect.ImmutableMap;
import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author cihat keser
 */
public class ModifyAliases extends GenericResultAbstractAction {

    protected ModifyAliases(Builder builder) {
        super(builder);

        List<Map> actions = new LinkedList<Map>();
        for (AliasMapping aliasMapping : builder.actions) {
            actions.addAll(aliasMapping.getData());
        }
        this.payload = ImmutableMap.<String, Object>of("actions", actions);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_aliases";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
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

        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
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
