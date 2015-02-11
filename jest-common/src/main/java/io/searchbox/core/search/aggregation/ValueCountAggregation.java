package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import static io.searchbox.core.search.aggregation.AggregationField.VALUE;

/**
 * @author cfstout
 */
public class ValueCountAggregation extends Aggregation<ValueCountAggregation> {

    public static final String TYPE = "value_count";

    private Long valueCount;

    public ValueCountAggregation(String name, JsonObject valueCountAggregation) {
        super(name, valueCountAggregation);
        valueCount = valueCountAggregation.get(String.valueOf(VALUE)).getAsLong();
    }

    public Long getValueCount() {
        return valueCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ValueCountAggregation that = (ValueCountAggregation) o;

        if (!valueCount.equals(that.valueCount)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return valueCount.hashCode();
    }
}
