package io.searchbox.action;

import io.searchbox.params.Parameters;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * All multi indices API support the ignore_indices option.
     * Setting it to missing will cause indices that do not exists
     * to be ignored from the execution. By default, when its not
     * set, the request will fail.
     *
     * @param ignoreIndices "none" (No indices / aliases will be excluded from a request) or
     *                      "missing" (Indices / aliases that are missing will be excluded from a request.)
     */
    public K ignoreIndices(String ignoreIndices) {
        setParameter(Parameters.IGNORE_INDICES, null);
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
