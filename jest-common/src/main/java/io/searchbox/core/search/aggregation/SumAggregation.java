package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import static io.searchbox.core.search.aggregation.AggregationField.VALUE;

/**
 * @author cfstout
 */
public class SumAggregation extends SingleValueAggregation<SumAggregation> {

    public static final String TYPE = "sum";

    public SumAggregation(String name, JsonObject sumAggregation) {
        super(name, sumAggregation);
    }

    public Double getSum() {
        return getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SumAggregation)) {
            return false;
        }
        return super.equals(o);
    }
}
