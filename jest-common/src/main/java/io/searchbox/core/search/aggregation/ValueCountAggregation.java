package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static io.searchbox.core.search.aggregation.AggregationField.VALUE;

/**
 * @author cfstout
 */
public class ValueCountAggregation extends MetricAggregation {

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

        ValueCountAggregation rhs = (ValueCountAggregation) o;
        return new EqualsBuilder()
                .append(getValueCount(), rhs.getValueCount())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getValueCount())
                .toHashCode();
    }
}
