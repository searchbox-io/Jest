package io.searchbox;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cihat keser
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMultiTypeActionBuilder<T extends Action, K> extends AbstractMultiIndexActionBuilder<T, K> {
    private Set<String> indexTypes = new HashSet<String>();

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
