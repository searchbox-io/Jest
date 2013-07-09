package io.searchbox;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cihat keser
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMultiIndexActionBuilder<T extends Action, K> extends AbstractAction.Builder<T, K> {
    protected Set<String> indexNames = new HashSet<String>();

    public K addIndex(String indexName) {
        this.indexNames.add(indexName);
        return (K) this;
    }

    public K addIndex(Collection<? extends String> indexNames) {
        this.indexNames.addAll(indexNames);
        return (K) this;
    }

    public String getJoinedIndices() {
        if (indexNames.size() > 0) {
            return StringUtils.join(indexNames, ",");
        } else {
            return "_all";
        }
    }

    abstract public T build();
}
