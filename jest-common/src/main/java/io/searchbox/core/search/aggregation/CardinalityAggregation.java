package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static io.searchbox.core.search.aggregation.AggregationField.VALUE;

/**
 * @author cfstout
 */
public class CardinalityAggregation extends MetricAggregation {

    public static final String TYPE = "cardinality";

    private Long cardinality;

    public CardinalityAggregation(String name, JsonObject cardinalityAggregation) {
        super(name, cardinalityAggregation);
        cardinality = cardinalityAggregation.get(String.valueOf(VALUE)).getAsLong();
    }

    /**
     * @return Cardinality if it was found and not null, null otherwise
     */
    public Long getCardinality() {
        return cardinality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CardinalityAggregation)) {
            return false;
        }

        CardinalityAggregation rhs = (CardinalityAggregation) o;
        return new EqualsBuilder()
                .append(getCardinality(), rhs.getCardinality())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getCardinality())
                .toHashCode();
    }
}

