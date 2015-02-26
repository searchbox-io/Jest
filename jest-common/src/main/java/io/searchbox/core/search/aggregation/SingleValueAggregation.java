package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static io.searchbox.core.search.aggregation.AggregationField.VALUE;

/**
 * @author cfstout
 */
public abstract class SingleValueAggregation <T extends SingleValueAggregation> extends Aggregation<T> {

    private Double value;

    protected SingleValueAggregation(String name, JsonObject singleValueAggregation) {
        super(name, singleValueAggregation);
        value = !singleValueAggregation.has(String.valueOf(VALUE)) || singleValueAggregation.get(String.valueOf(VALUE)).isJsonNull()?
                null : singleValueAggregation.get(String.valueOf(VALUE)).getAsDouble();
    }

    /**
     * @return value if it was found and not null, null otherwise
     */
    protected Double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SingleValueAggregation)) {
            return false;
        }

        SingleValueAggregation rhs = (SingleValueAggregation) o;
        return new EqualsBuilder()
                .append(getValue(), rhs.getValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getValue())
                .toHashCode();
    }
}
