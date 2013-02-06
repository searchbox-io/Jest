package io.searchbox.core.search.facet;

import java.util.Map;

/**
 * @author ferhat
 */
public class FilterFacet extends Facet {
    public static final String TYPE = "filter";

    private Long count;

    public FilterFacet(String name, Map filterFacet) {
        this.name = name;
        this.count = ((Double) filterFacet.get("count")).longValue();
    }

    public Long getCount() {
        return count;
    }
}
