package io.searchbox.core.search.facet;

import com.google.gson.JsonObject;

/**
 * @author ferhat
 */
public class FilterFacet extends Facet {
    public static final String TYPE = "filter";

    private Long count;

    public FilterFacet(String name, JsonObject filterFacet) {
        this.name = name;
        this.count = filterFacet.get("count").getAsLong();
    }

    public Long getCount() {
        return count;
    }
}
