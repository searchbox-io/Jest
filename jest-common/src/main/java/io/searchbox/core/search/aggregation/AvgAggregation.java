package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

/**
 * @author cfstout
 */
public class AvgAggregation extends SingleValueAggregation {

    public static final String TYPE = "avg";

    public AvgAggregation(String name, JsonObject avgAggregation) {
        super(name, avgAggregation);
    }

    /**
     * @return Average if it was found and not null, null otherwise
     */
    public Double getAvg() {
        return getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        return super.equals(obj);
    }
}
