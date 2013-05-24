package io.searchbox;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cihat keser
 */
public abstract class AbstractMultiIndexActionBuilder<T extends Action> {
    private Set<String> indexNames = new HashSet<String>();
    private Set<String> indexTypes = new HashSet<String>();

    public AbstractMultiIndexActionBuilder addIndexName(String indexName) {
        indexNames.add(indexName);
        return this;
    }

    public AbstractMultiIndexActionBuilder addIndexName(Collection<String> indexNames) {
        indexNames.addAll(indexNames);
        return this;
    }

    public AbstractMultiIndexActionBuilder addIndexType(Collection<String> indexTypes) {
        indexTypes.addAll(indexTypes);
        return this;
    }

    public AbstractMultiIndexActionBuilder addIndexType(String indexType) {
        indexTypes.add(indexType);
        return this;
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
