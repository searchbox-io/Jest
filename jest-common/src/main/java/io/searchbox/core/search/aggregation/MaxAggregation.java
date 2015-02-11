package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import java.util.Map;

/**
 * @author cfstout
 */
public class MaxAggregation extends SingleValueAggregation<MaxAggregation> {

    public static final String TYPE = "max";

    public MaxAggregation(String name, JsonObject maxAggregation) {
        super(name, maxAggregation);
    }

    /**
     * @return Max if it was found and not null, null otherwise
     */
    public Double getMax() {
        return getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaxAggregation)) {
            return false;
        }
        return super.equals(o);
    }
}
