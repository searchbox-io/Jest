package io.searchbox;

import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CihatKeser
 */
public abstract class AbstractMultiIndexActionBuilder<T extends Action> {
    private Set<String> indexNames = new HashSet<String>();
    private Set<String> indexTypes = new HashSet<String>();

    public AbstractMultiIndexActionBuilder addIndexName(String indexName) {
        indexNames.add(indexName);
        return this;
    }

    public AbstractMultiIndexActionBuilder addIndexType(String indexType) {
        indexTypes.add(indexType);
        return this;
    }

    protected String getJoinedIndexNames() {
        if (indexNames.size() > 0) {
            return StringUtils.join(indexNames, ",");
        } else {
            return "_all";
        }
    }

    protected String getJoinedIndexTypes() {
        return StringUtils.join(indexTypes, ",");
    }

    abstract public T build();
}
