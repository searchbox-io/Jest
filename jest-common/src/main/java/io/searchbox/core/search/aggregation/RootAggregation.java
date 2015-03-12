package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

/**
 * Place holder class used to represent the root aggregation
 * returned to the user for processing.
 * @author cfstout
 */
public class RootAggregation extends MetricAggregation {

    public RootAggregation(String name, JsonObject root) {
        super(name, root);
    }
}
