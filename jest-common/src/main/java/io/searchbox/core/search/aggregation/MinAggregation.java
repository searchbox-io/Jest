package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import static io.searchbox.core.search.aggregation.AggregationField.VALUE;

/**
 * @author cfstout
 */
public class MinAggregation extends SingleValueAggregation<MinAggregation> {

    public static final String TYPE = "min";

    public MinAggregation(String name, JsonObject minAggregation) {
        super(name, minAggregation);
    }

    /**
     * @return Min if it was found and not null, null otherwise
     */
    public Double getMin() {
        return getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MinAggregation)) {
            return false;
        }
        return super.equals(o);
    }
}
