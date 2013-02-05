package io.searchbox.core.search.facet;

import java.util.Map;

/**
 * @author ferhat
 */
public class FilterFacet {
    public static final String TYPE = "filter";

    private String name;
    private Long count;

    public FilterFacet(String name, Map filterFacet) {
        this.name = name;
        this.count = ((Double) filterFacet.get("count")).longValue();
    }

    public String getName() {
        return name;
    }

    public Long getCount() {
        return count;
    }
}
