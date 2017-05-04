package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import java.util.Objects;

/**
 * @author cfstout
 */
public class SumAggregation extends SingleValueAggregation {

    public static final String TYPE = "sum";

    public SumAggregation(String name, JsonObject sumAggregation) {
        super(name, sumAggregation);
    }

    public Double getSum() {
        return getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), TYPE);
    }
}
