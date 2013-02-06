package io.searchbox.core.search.facet;

import java.util.Map;

/**
 * @author ferhat
 */
public class QueryFacet extends Facet {

    public static final String TYPE = "query";

    private Long count;

    public QueryFacet(String name, Map filterFacet) {
        this.name = name;
        this.count = ((Double) filterFacet.get("count")).longValue();
    }

    public Long getCount() {
        return count;
    }
}
