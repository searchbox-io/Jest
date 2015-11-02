package io.searchbox.action;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author cihat keser
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMultiTypeActionBuilder<T extends Action, K> extends AbstractMultiIndexActionBuilder<T, K> {
    private Set<String> indexTypes = new LinkedHashSet<String>();

    public K addType(Collection<? extends String> indexTypes) {
        this.indexTypes.addAll(indexTypes);
        return (K) this;
    }

    public K addType(String indexType) {
        this.indexTypes.add(indexType);
        return (K) this;
    }

    public String getJoinedTypes() {
        return StringUtils.join(indexTypes, ",");
    }
}
