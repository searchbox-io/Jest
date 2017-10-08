package io.searchbox.indices.aliases;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author cihat keser
 */
public abstract class AbstractAliasMappingBuilder<T extends AliasMapping, K> {
    protected Map<String, Object> filter;
    protected String alias;
    protected List<String> indices = new LinkedList<String>();
    protected List<String> searchRouting = new LinkedList<String>();
    protected List<String> indexRouting = new LinkedList<String>();

    public AbstractAliasMappingBuilder(Collection<String> indices, String alias) {
        this.indices.addAll(indices);
        this.alias = alias;
    }

    public AbstractAliasMappingBuilder(String index, String alias) {
        this.indices.add(index);
        this.alias = alias;
    }

    public K addIndex(String index) {
        this.indices.add(index);
        return (K) this;
    }

    public K addIndex(Collection<String> indices) {
        this.indices.addAll(indices);
        return (K) this;
    }

    /**
     * Aliases with filters provide an easy way to create different “views” of the same index.
     * The filter can be defined using Query DSL and is applied to all Search, Count,
     * Delete By Query and More Like This operations with this alias.
     */
    public K setFilter(Map<String, Object> source) {
        this.filter = source;
        return (K) this;
    }

    /**
     * This method will add the given routing as both search & index routing.
     */
    public K addRouting(String routing) {
        this.indexRouting.add(routing);
        this.searchRouting.add(routing);
        return (K) this;
    }

    /**
     * This method will add the given routings as both search & index routing.
     */
    public K addRouting(List<String> routings) {
        this.indexRouting.addAll(routings);
        this.searchRouting.addAll(routings);
        return (K) this;
    }

    public K addSearchRouting(String searchRouting) {
        this.searchRouting.add(searchRouting);
        return (K) this;
    }

    public K addSearchRouting(List<String> searchRoutings) {
        this.searchRouting.addAll(searchRoutings);
        return (K) this;
    }

    public K addIndexRouting(String indexRouting) {
        this.indexRouting.add(indexRouting);
        return (K) this;
    }

    public K addIndexRouting(List<String> indexRoutings) {
        this.indexRouting.addAll(indexRoutings);
        return (K) this;
    }

    public abstract T build();
}