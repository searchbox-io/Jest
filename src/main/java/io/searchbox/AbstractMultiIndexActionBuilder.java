package io.searchbox;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cihat keser
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMultiIndexActionBuilder<T extends Action, K> {
    private Set<String> indexNames = new HashSet<String>();
    private Set<String> indexTypes = new HashSet<String>();

    public K addIndexName(String indexName) {
        indexNames.add(indexName);
        return (K) this;
    }

    public K addIndexName(Collection<String> indexNames) {
        indexNames.addAll(indexNames);
        return (K) this;
    }

    public K addIndexType(Collection<String> indexTypes) {
        indexTypes.addAll(indexTypes);
        return (K) this;
    }

    public K addIndexType(String indexType) {
        indexTypes.add(indexType);
        return (K) this;
    }

    public String getJoinedIndexNames() {
        if (indexNames.size() > 0) {
            return StringUtils.join(indexNames, ",");
        } else {
            return "_all";
        }
    }

    public String getJoinedIndexTypes() {
        return StringUtils.join(indexTypes, ",");
    }

    abstract public T build();
}
