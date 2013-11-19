package io.searchbox.core.search.facet;

import com.google.gson.JsonObject;

/**
 * @author ferhat
 */
public class QueryFacet extends Facet {

    public static final String TYPE = "query";

    private Long count;

    public QueryFacet(String name, JsonObject filterFacet) {
        this.name = name;
        this.count = filterFacet.get("count").getAsLong();
    }

    public Long getCount() {
        return count;
    }
}
